package net.werdenrc5.villagerdance.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue CHANGE_TEXTURE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("villagerdance");

        CHANGE_TEXTURE = builder
            .comment("Swap the villager texture while dancing.")
            .define("changeTexture", true);

        builder.pop();
        SPEC = builder.build();
    }
}
