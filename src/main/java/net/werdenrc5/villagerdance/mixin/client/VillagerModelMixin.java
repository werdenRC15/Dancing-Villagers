package net.werdenrc5.villagerdance.mixin.client;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.werdenrc5.villagerdance.client.DancePoser;
import net.werdenrc5.villagerdance.dance.DanceStyle;
import net.werdenrc5.villagerdance.dance.IDancingVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(VillagerModel.class)
public abstract class VillagerModelMixin<T extends Entity> extends HierarchicalModel<T> implements HeadedModel, VillagerHeadModel {
    @Unique
    private boolean villagerdance$posed;

    @Unique
    private ModelPart villagerdance$dummy;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", at = @At("HEAD"))
    private void villagerdance$setupAnim(T entity, float limbSwing, float limbSwingAmount,
                                     float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (this.villagerdance$posed) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            this.villagerdance$posed = false;
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", at = @At("TAIL"))
    private void villagerdance$setupAnim2(T entity, float limbSwing, float limbSwingAmount,
                                    float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof IDancingVillager dancer)) return;

        float progress = Mth.clamp(dancer.getDanceProgress(ageInTicks - entity.tickCount), 0.0F, 1.0F);
        if (progress <= 0.0F) return;

        boolean dancing = dancer.isDancing();
        int animation = dancing ? dancer.getDanceAnimation() : dancer.getPreviousDanceAnimation();
        if (animation < 0) return;

        int previous = dancing ? dancer.getPreviousDanceAnimation() : -1;

        DancePoser.apply(
            DanceStyle.byId(animation),
            previous >= 0 ? DanceStyle.byId(previous) : null,
            dancing ? dancer.getDanceBlend(ageInTicks) : 1.0F,
            progress,
            this.villagerdance$part("head"),
            this.villagerdance$part("body"),
            this.villagerdance$part("arms"),
            this.villagerdance$part("right_leg"),
            this.villagerdance$part("left_leg"),
            ageInTicks,
            dancing ? dancer.getDanceSpeed() : dancer.getPreviousDanceSpeed(),
            dancer.getPreviousDanceSpeed()
        );

        this.villagerdance$posed = true;
    }

    @Unique
    private ModelPart villagerdance$part(String name) {
        ModelPart root = this.root();
        if (root.hasChild(name)) {
            return root.getChild(name);
        }
        if (this.villagerdance$dummy == null) {
            this.villagerdance$dummy = new ModelPart(List.of(), Map.of());
        }
        return this.villagerdance$dummy;
    }
}
