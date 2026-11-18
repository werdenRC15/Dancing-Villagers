package net.werdenrc5.villagerdance;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.werdenrc5.villagerdance.config.ClientConfig;
import net.werdenrc5.villagerdance.config.DanceConfig;
import org.slf4j.Logger;

@Mod(VillagerDanceMod.MOD_ID)
public class VillagerDanceMod {
    public static final String MOD_ID = "villagerdance";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerDanceMod() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.SERVER, DanceConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
