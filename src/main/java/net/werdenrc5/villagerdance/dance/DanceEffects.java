package net.werdenrc5.villagerdance.dance;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;
import net.werdenrc5.villagerdance.config.DanceConfig;

public final class DanceEffects {
    private DanceEffects() {
    }

    public static void tick(Villager villager, ServerLevel level, boolean notes) {
        if (villager.tickCount % 20 != 0) return;

        RandomSource random = villager.getRandom();

        if (notes && DanceConfig.ENABLE_PARTICLES.get() && random.nextInt(2) == 0) {
            int count = DanceConfig.PARTICLE_DENSITY.get();
            for (int i = 0; i < count; i++) {
                double x = villager.getX() + (random.nextDouble() - 0.5) * 0.8;
                double y = villager.getY() + random.nextDouble() * 1.5 + 0.5;
                double z = villager.getZ() + (random.nextDouble() - 0.5) * 0.8;
                level.sendParticles(ParticleTypes.NOTE, x, y, z, 1, 0.1, 0.1, 0.1, 0.02);
            }
        }

        if (DanceConfig.ENABLE_SOUNDS.get() && random.nextInt(4) == 0) {
            float volume = DanceConfig.SOUND_VOLUME.get().floatValue();
            float pitch = 0.8F + random.nextFloat() * 0.4F;
            level.playSound(null, villager.getX(), villager.getY(), villager.getZ(),
                SoundEvents.VILLAGER_CELEBRATE, SoundSource.NEUTRAL, volume, pitch);
        }
    }
}