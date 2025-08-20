package net.werdenrc5.villagerdance.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.werdenrc5.villagerdance.config.DanceConfig;

import java.util.Random;

public class DanceEffectsUtil {
    private static final Random RANDOM = new Random();
    
public static void spawnDanceParticles(LivingEntity entity, ParticleOptions particleType) {
    if (!DanceConfig.SERVER.enableParticles.get() || !(entity.level() instanceof ServerLevel serverLevel)) {
        return;
    }
    
    int count = DanceConfig.SERVER.particleDensity.get();
    
    for (int i = 0; i < count; i++) {
        double offsetX = (RANDOM.nextDouble() - 0.5) * 1.0;
        double offsetY = RANDOM.nextDouble() * 1.5 + 0.5;
        double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.0;
        
        serverLevel.sendParticles(
            ParticleTypes.NOTE,
            entity.getX() + offsetX,
            entity.getY() + offsetY,
            entity.getZ() + offsetZ,
            1,
            0.1, 0.1, 0.1, 0.02
        );
    }
}
    
    public static void playDanceSound(LivingEntity entity, SoundEvent sound, float volume) {
        if (!DanceConfig.SERVER.enableSounds.get()) {
            return;
        }
        
        SoundEvent soundToPlay = sound != null ? sound : getRandomDanceSound();
        float configVolume = DanceConfig.SERVER.soundVolume.get().floatValue();
        float pitch = 0.8f + RANDOM.nextFloat() * 0.4f; // Random pitch between 0.8 and 1.2
        
        entity.level().playSound(
            null,
            entity.getX(), entity.getY(), entity.getZ(),
            soundToPlay,
            SoundSource.NEUTRAL,
            volume * configVolume,
            pitch
        );
    }
    
    private static SoundEvent getRandomDanceSound() {
        SoundEvent[] sounds = {
            SoundEvents.VILLAGER_YES,
            SoundEvents.VILLAGER_CELEBRATE,
            SoundEvents.VILLAGER_AMBIENT
        };
        return sounds[RANDOM.nextInt(sounds.length)];
    }
    
    public static boolean shouldTriggerEffect(int chance) {
        return RANDOM.nextInt(chance) == 0;
    }
}