package net.werdenrc5.villagerdance.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.data.DanceDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public class RaidMixin {

    @Shadow
    public boolean isVictory() {
        throw new AssertionError();
    }

    @Shadow
    public BlockPos getCenter() {
        throw new AssertionError();
    }

    @Shadow
    public Level getLevel() {
        throw new AssertionError();
    }

    @Unique
    private boolean hasTriggeredVictoryDance = false;
    @Unique
    private boolean wasVictory = false;
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void onRaidTick(CallbackInfo ci) {
        if (!DanceConfig.SERVER.danceAfterRaidVictory.get()) {
            return;
        }
        
        boolean currentVictory = isVictory();
        
        if (currentVictory && !hasTriggeredVictoryDance) {
            hasTriggeredVictoryDance = true;

            Level level = getLevel();
            BlockPos center = getCenter();
            double radius = DanceConfig.SERVER.danceRadius.get();
            
            AABB searchArea = new AABB(center).inflate(radius);
            level.getEntitiesOfClass(Villager.class, searchArea).forEach(villager -> {
                if (!DanceConfig.SERVER.babiesCanDance.get() && villager.isBaby()) {
                    return;
                }
                
                DanceDataManager.startDancing(villager, 400, false);
            });
        }
        
        if (wasVictory && !currentVictory) {
            Level level = getLevel();
            BlockPos center = getCenter();
            double radius = DanceConfig.SERVER.danceRadius.get();
            
            AABB searchArea = new AABB(center).inflate(radius);
            level.getEntitiesOfClass(Villager.class, searchArea).forEach(villager -> {
                DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
                if (data.isDancing) {
                    DanceDataManager.stopDancing(villager);
                }
            });
            
            hasTriggeredVictoryDance = false;
        }
        
        wasVictory = currentVictory;
    }
}