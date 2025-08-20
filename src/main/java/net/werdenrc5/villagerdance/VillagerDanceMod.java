package net.werdenrc5.villagerdance;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.data.DanceDataManager;
import net.werdenrc5.villagerdance.network.NetworkHandler;
import org.slf4j.Logger;

@Mod(VillagerDanceMod.MOD_ID)
public class VillagerDanceMod {
    public static final String MOD_ID = "villagerdance";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public VillagerDanceMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DanceConfig.SERVER_SPEC);
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DanceDataManager());
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.register();
            LOGGER.info("Network handler registered");
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup complete");
    }
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Villager Dance Mod is active on server");
    }
}