package net.werdenrc5.villagerdance.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import net.werdenrc5.villagerdance.config.ClientConfig;
import net.werdenrc5.villagerdance.dance.IDancingVillager;

public class DanceLayer extends RenderLayer<Villager, VillagerModel<Villager>> {
    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(VillagerDanceMod.MOD_ID, "textures/entity/villager/villager_dancing.png"),
        new ResourceLocation(VillagerDanceMod.MOD_ID, "textures/entity/villager/villager_dancing2.png")
    };

    public DanceLayer(RenderLayerParent<Villager, VillagerModel<Villager>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Villager villager,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!ClientConfig.CHANGE_TEXTURE.get() || villager.isInvisible()) return;
        IDancingVillager dancer = (IDancingVillager) villager;
        if (!dancer.isDancing()) return;

        ResourceLocation texture = TEXTURES[Math.floorMod(dancer.getDanceVariant(), TEXTURES.length)];
        renderColoredCutoutModel(this.getParentModel(), texture, poseStack, buffer, packedLight, villager,
            1.0F, 1.0F, 1.0F);
    }
}
