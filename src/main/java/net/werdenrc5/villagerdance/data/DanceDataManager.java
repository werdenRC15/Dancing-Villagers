package net.werdenrc5.villagerdance.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.mixin.accessor.JukeboxBlockEntityAccessor;
import net.werdenrc5.villagerdance.network.NetworkHandler;
import net.werdenrc5.villagerdance.util.DanceEffectsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DanceDataManager {
    private static final Map<UUID, DanceData> danceMap = new HashMap<>();
    
public static class DanceData {
    public int danceTime = 0;
    public float danceSpeed = 10.0f;
    public boolean isDancing = false;
    public int lastEffectTick = 0;
    public boolean isPiglinDance = false;
    public boolean isJukeboxDancing = false;
    
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DanceTime", danceTime);
        tag.putFloat("DanceSpeed", danceSpeed);
        tag.putBoolean("IsDancing", isDancing);
        tag.putBoolean("IsPiglinDance", isPiglinDance);
        return tag;
    }
    
    public void deserializeNBT(CompoundTag tag) {
        this.danceTime = tag.getInt("DanceTime");
        this.danceSpeed = tag.getFloat("DanceSpeed");
        this.isDancing = tag.getBoolean("IsDancing");
        this.isPiglinDance = tag.getBoolean("IsPiglinDance");
    }
}
    
    public static DanceData getDanceData(Villager villager) {
        return danceMap.computeIfAbsent(villager.getUUID(), k -> new DanceData());
    }
    
    public static void removeDanceData(Villager villager) {
        danceMap.remove(villager.getUUID());
    }
    
public static void startDancing(Villager villager, int duration, boolean isJukeboxDancing) {
    DanceData data = getDanceData(villager);
    data.danceTime = duration;
    data.isDancing = true;
    data.isJukeboxDancing = isJukeboxDancing;
    
    data.isPiglinDance = villager.getRandom().nextBoolean();
    
    if (DanceConfig.SERVER.randomizeDanceSpeeds.get()) {
        float min = DanceConfig.SERVER.minimumDanceSpeed.get().floatValue();
        float max = DanceConfig.SERVER.maximumDanceSpeed.get().floatValue();
        data.danceSpeed = min + villager.getRandom().nextFloat() * (max - min);
    }
    
    if (!villager.level().isClientSide()) {
        NetworkHandler.syncDanceData(villager, data);
    }
    
    VillagerDanceMod.LOGGER.debug("Villager {} started {} dancing for {} ticks", 
        villager.getUUID(), data.isPiglinDance ? "piglin" : "regular", duration);
}
    
    public static void stopDancing(Villager villager) {
        DanceData data = getDanceData(villager);
        data.danceTime = 0;
        data.isDancing = false;
        
        if (!villager.level().isClientSide()) {
            NetworkHandler.syncDanceData(villager, data);
        }
        
        VillagerDanceMod.LOGGER.debug("Villager {} stopped dancing", villager.getUUID());
    }
    
    @SubscribeEvent
    public void onVillagerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Villager villager) || villager.level().isClientSide()) {
            return;
        }
        
        DanceData data = getDanceData(villager);
        
        if (data.isDancing && data.danceTime > 0) {
            data.danceTime--;
            
            if (villager.tickCount % 20 == 0) {
                if (DanceEffectsUtil.shouldTriggerEffect(3)) {
                    DanceEffectsUtil.spawnDanceParticles(villager, null);
                }
                if (DanceEffectsUtil.shouldTriggerEffect(4)) {
                    DanceEffectsUtil.playDanceSound(villager, null, 0.7f);
                }
            }
            
if (data.danceTime <= 0 && !data.isJukeboxDancing) {
    stopDancing(villager);
}
        }
    }
    
@SubscribeEvent
public void onEntityJoinLevel(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof Villager villager) {
        if (event.getEntity().isRemoved()) {
            removeDanceData(villager);
        } else if (!villager.level().isClientSide() && DanceConfig.SERVER.danceNearJukebox.get()) {
            BlockPos pos = villager.blockPosition();
            double radius = DanceConfig.SERVER.danceRadius.get();
            
            for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-(int)radius, -(int)radius, -(int)radius),
                pos.offset((int)radius, (int)radius, (int)radius))) {
                
                if (villager.level().getBlockEntity(nearbyPos) instanceof JukeboxBlockEntity jukebox) {
                    if (!jukebox.isEmpty() && ((JukeboxBlockEntityAccessor) jukebox).isPlaying()) {
                        if (!DanceConfig.SERVER.babiesCanDance.get() && villager.isBaby()) {
                            continue;
                        }
                        
                        DanceDataManager.startDancing(villager, Integer.MAX_VALUE, true);
                        NetworkHandler.syncDanceData(villager, getDanceData(villager));
                        break;
                    }
                }
            }
        }
    }
}
}