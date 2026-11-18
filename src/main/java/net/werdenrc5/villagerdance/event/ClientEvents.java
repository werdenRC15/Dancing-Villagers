package net.werdenrc5.villagerdance.event;

import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import net.werdenrc5.villagerdance.client.render.layer.DanceLayer;

@Mod.EventBusSubscriber(modid = VillagerDanceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        Object renderer = event.getRenderer(EntityType.VILLAGER);
        if (renderer instanceof VillagerRenderer villagerRenderer) {
            villagerRenderer.addLayer(new DanceLayer(villagerRenderer));
        }
    }
}