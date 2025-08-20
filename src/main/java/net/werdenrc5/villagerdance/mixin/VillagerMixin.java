package net.werdenrc5.villagerdance.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.Villager;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.data.DanceDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin {
    
@Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
private void onHandleEntityEvent(byte event, CallbackInfo ci) {
    if (event == 14) {
        Villager self = (Villager) (Object) this;
        
        if (!DanceConfig.SERVER.babiesCanDance.get() && self.isBaby()) {
            return;
        }
        
        ci.cancel();
        
        if (!self.level().isClientSide()) {
            DanceDataManager.startDancing(self, 200, false);
        }
    }
}

@Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
private void preventAIStepDuringDance(CallbackInfo ci) {
    Villager self = (Villager) (Object) this;
    DanceDataManager.DanceData data = DanceDataManager.getDanceData(self);
    if (data.isDancing) {
        ci.cancel();
    }
}

@Inject(method = "tick", at = @At("TAIL"))
private void lookAtPlayerDuringJukeboxDance(CallbackInfo ci) {
    Villager self = (Villager) (Object) this;
    DanceDataManager.DanceData data = DanceDataManager.getDanceData(self);
    
    if (data.isDancing && data.isJukeboxDancing && !self.level().isClientSide()) {
        var nearestPlayer = self.level().getNearestPlayer(self, 8.0);
        if (nearestPlayer != null) {
            double dx = nearestPlayer.getX() - self.getX();
            double dz = nearestPlayer.getZ() - self.getZ();
            float yaw = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            
            self.setYRot(Mth.wrapDegrees(yaw));
            self.yRotO = self.getYRot();
            self.setYHeadRot(self.getYRot());
            self.yHeadRotO = self.getYHeadRot();
        }
    }
}
}