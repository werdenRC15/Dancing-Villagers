package net.werdenrc5.villagerdance.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = VillagerDanceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DanceConfig {
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER = specPair.getLeft();
        SERVER_SPEC = specPair.getRight();
    }

    public static class ServerConfig {
        public final ForgeConfigSpec.IntValue danceRadius;
        public final ForgeConfigSpec.BooleanValue randomizeDanceSpeeds;
        public final ForgeConfigSpec.DoubleValue minimumDanceSpeed;
        public final ForgeConfigSpec.DoubleValue maximumDanceSpeed;
        
        // Visual and audio effects
        public final ForgeConfigSpec.BooleanValue enableParticles;
        public final ForgeConfigSpec.BooleanValue enableSounds;
        public final ForgeConfigSpec.IntValue particleDensity;
        public final ForgeConfigSpec.DoubleValue soundVolume;
        
        // Special features
        public final ForgeConfigSpec.BooleanValue babiesCanDance;
        public final ForgeConfigSpec.BooleanValue danceNearJukebox;
        public final ForgeConfigSpec.BooleanValue danceAfterRaidVictory;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Villager Dance Mod Configuration")
                   .push("villagerdance");
            
            builder.comment("Core dance behavior settings")
                   .push("behavior");
            
            danceRadius = builder
                .comment("Radius in blocks around trigger point where villagers will start dancing")
                .defineInRange("danceRadius", 32, 8, 128);
                
            randomizeDanceSpeeds = builder
                .comment("Give each villager a random dance speed")
                .define("randomizeDanceSpeeds", true);
                
            minimumDanceSpeed = builder
                .comment("Minimum dance animation speed")
                .defineInRange("minimumDanceSpeed", 5.0, 1.0, 20.0);
                
            maximumDanceSpeed = builder
                .comment("Maximum dance animation speed")
                .defineInRange("maximumDanceSpeed", 15.0, 1.0, 30.0);
            
            builder.pop();
            
            builder.comment("Visual and audio effects settings")
                   .push("effects");
            
            enableParticles = builder
                .comment("Show particle effects when villagers dance")
                .define("enableParticles", true);
            
            enableSounds = builder
                .comment("Play sounds when villagers dance")
                .define("enableSounds", true);
                
            particleDensity = builder
                .comment("Number of particles per effect burst")
                .defineInRange("particleDensity", 3, 1, 10);
                
            soundVolume = builder
                .comment("Volume multiplier for dance sounds")
                .defineInRange("soundVolume", 0.8, 0.0, 2.0);
            
            builder.pop();
            
            builder.comment("Special feature toggles")
                   .push("features");
            
            babiesCanDance = builder
                .comment("Allow baby villagers to dance")
                .define("babiesCanDance", true);
                
            danceNearJukebox = builder
                .comment("Villagers dance when jukebox plays music nearby")
                .define("danceNearJukebox", true);
                
            danceAfterRaidVictory = builder
                .comment("Villagers dance after successfully defending against raids")
                .define("danceAfterRaidVictory", true);
            
            builder.pop();
            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        VillagerDanceMod.LOGGER.info("Loaded config: {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        VillagerDanceMod.LOGGER.info("Reloaded config");
    }
}