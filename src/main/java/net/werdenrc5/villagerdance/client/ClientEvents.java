package net.werdenrc5.villagerdance.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.data.DanceDataManager;

@Mod.EventBusSubscriber(modid = VillagerDanceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;
        
        mc.level.getEntitiesOfClass(Villager.class, mc.player.getBoundingBox().inflate(32)).forEach(villager -> {
            DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
            
            if (data.isDancing && data.danceTime > 0) {
                if (villager.tickCount % 10 == 0 && DanceConfig.SERVER.enableParticles.get()) {
                    double x = villager.getX() + (villager.getRandom().nextDouble() - 0.5) * 0.8;
                    double y = villager.getY() + villager.getRandom().nextDouble() * 1.5 + 0.5;
                    double z = villager.getZ() + (villager.getRandom().nextDouble() - 0.5) * 0.8;
                    
                    mc.level.addParticle(ParticleTypes.NOTE, x, y, z, 
                        villager.getRandom().nextDouble(), 0.0, 0.0);
                }
            }
        });
    }
}