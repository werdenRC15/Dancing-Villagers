package net.werdenrc5.villagerdance.mixin.client;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.werdenrc5.villagerdance.data.DanceDataManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(VillagerModel.class)
public class VillagerModelMixin {
    
    @Shadow @Final private ModelPart head;
    @Shadow @Final private ModelPart rightLeg;
    @Shadow @Final private ModelPart leftLeg;

    private ModelPart getArms() {
        return ((VillagerModel<?>) (Object) this).root().getChild("arms");
    }

    private ModelPart getBody() {
        return ((VillagerModel<?>) (Object) this).root().getChild("body");
    }
    
@Inject(method = "setupAnim", at = @At("TAIL"))
private void setupDanceAnimation(Entity entity, float limbSwing, float limbSwingAmount,
                                 float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

    if (!(entity instanceof Villager villager)) return;
    
    DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
    
    if (data.isDancing && data.danceTime > 0) {
        if (data.isPiglinDance) {
            if (entity.level() != null) {
                var nearestPlayer = entity.level().getNearestPlayer(entity, 8.0);
                if (nearestPlayer != null) {
                    double dx = nearestPlayer.getX() - entity.getX();
                    double dz = nearestPlayer.getZ() - entity.getZ();
                    float yaw = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
                    head.yRot = Mth.wrapDegrees(yaw - entity.getYRot()) * 0.017453292F * 0.5F;
                }
            }
            
            float f3 = ageInTicks / 60.0F;
            head.x = Mth.sin(f3 * 10.0F);
            head.y = Mth.sin(f3 * 40.0F) + 0.4F;
            
            ModelPart body = getBody();
            body.y = Mth.sin(f3 * 40.0F) * 0.35F;
        } else {
            if (entity.level() != null) {
                var nearestPlayer = entity.level().getNearestPlayer(entity, 8.0);
                if (nearestPlayer != null) {
                    double dx = nearestPlayer.getX() - entity.getX();
                    double dz = nearestPlayer.getZ() - entity.getZ();
                    float yaw = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
                    head.yRot = Mth.wrapDegrees(yaw - entity.getYRot()) * 0.017453292F * 0.5F;
                }
            }
            
            float danceProgress = ageInTicks * data.danceSpeed * 0.05f;
            
            head.xRot += Mth.sin(danceProgress * 0.8f) * 0.2f;
            head.yRot += Mth.sin(danceProgress * 0.4f) * 0.3f;
            head.zRot = Mth.cos(danceProgress * 0.6f) * 0.15f;
            
            ModelPart arms = getArms();
            if (arms != null) {
                arms.xRot = -0.75f + Mth.sin(danceProgress) * 0.4f;
                arms.yRot = 0.0f;
                arms.zRot = Mth.sin(danceProgress * 1.2f) * 0.3f;
                arms.y = 3.0f + Mth.sin(danceProgress * 0.7f) * 0.5f;
            }
            
            ModelPart body = getBody();
            body.yRot = Mth.sin(danceProgress * 0.5f) * 0.15f;
            
            rightLeg.xRot = Mth.sin(danceProgress * 1.5f) * 0.4f;
            leftLeg.xRot = Mth.sin(danceProgress * 1.5f + Mth.PI) * 0.4f;
            
            rightLeg.y = 12.0f + Math.max(0, Mth.sin(danceProgress * 1.5f)) * 0.5f;
            leftLeg.y = 12.0f + Math.max(0, Mth.sin(danceProgress * 1.5f + Mth.PI)) * 0.5f;
        }
    } else {
        resetDanceAnimation();
    }
}

private void resetDanceAnimation() {
    
    ModelPart arms = getArms();
    if (arms != null) {
        arms.xRot = -0.75F;
        arms.yRot = 0.0F;
        arms.zRot = 0.0F;
        arms.x = 0.0F;
        arms.y = 3.0F;
        arms.z = -1.0F;
    }
    
    ModelPart body = getBody();
    if (body != null) {
        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.zRot = 0.0F;
        body.x = 0.0F;
        body.y = 0.0F;
        body.z = 0.0F;
    }
    
    rightLeg.yRot = 0.0F;
    rightLeg.zRot = 0.0F;
    rightLeg.x = -2.0F;
    rightLeg.y = 12.0F;
    rightLeg.z = 0.0F;
    
    leftLeg.yRot = 0.0F;
    leftLeg.zRot = 0.0F;
    leftLeg.x = 2.0F;
    leftLeg.y = 12.0F;
    leftLeg.z = 0.0F;
    
    head.x = 0.0F;
    head.y = 0.0F;
    head.z = 0.0F;
}

}