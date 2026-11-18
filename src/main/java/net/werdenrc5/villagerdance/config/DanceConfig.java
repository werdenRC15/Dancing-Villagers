package net.werdenrc5.villagerdance.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class DanceConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue DANCE_RADIUS;
    public static final ForgeConfigSpec.IntValue RAID_RADIUS;
    public static final ForgeConfigSpec.IntValue DANCE_SECONDS;
    public static final ForgeConfigSpec.BooleanValue RANDOMIZE_SPEEDS;
    public static final ForgeConfigSpec.DoubleValue MIN_SPEED;
    public static final ForgeConfigSpec.DoubleValue MAX_SPEED;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PARTICLES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SOUNDS;
    public static final ForgeConfigSpec.IntValue PARTICLE_DENSITY;
    public static final ForgeConfigSpec.DoubleValue SOUND_VOLUME;
    public static final ForgeConfigSpec.BooleanValue BABIES_CAN_DANCE;
    public static final ForgeConfigSpec.BooleanValue DANCE_NEAR_JUKEBOX;
    public static final ForgeConfigSpec.BooleanValue DANCE_AFTER_RAID;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("villagerdance");

        DANCE_RADIUS = builder.defineInRange("danceRadius", 16, 4, 64);
        RAID_RADIUS = builder.defineInRange("raidDanceRadius", 48, 8, 128);
        DANCE_SECONDS = builder.defineInRange("danceSeconds", 20, 1, 300);
        RANDOMIZE_SPEEDS = builder.define("randomizeSpeeds", true);
        MIN_SPEED = builder.defineInRange("minSpeed", 0.8, 0.1, 2.0);
        MAX_SPEED = builder.defineInRange("maxSpeed", 1.5, 0.1, 3.0);
        ENABLE_PARTICLES = builder.define("enableParticles", true);
        ENABLE_SOUNDS = builder.define("enableSounds", true);
        PARTICLE_DENSITY = builder.defineInRange("particleDensity", 3, 1, 10);
        SOUND_VOLUME = builder.defineInRange("soundVolume", 0.7, 0.0, 2.0);
        BABIES_CAN_DANCE = builder.define("babiesCanDance", true);
        DANCE_NEAR_JUKEBOX = builder.define("danceNearJukebox", true);
        DANCE_AFTER_RAID = builder.define("danceAfterRaid", true);

        builder.pop();
        SPEC = builder.build();
    }
}
