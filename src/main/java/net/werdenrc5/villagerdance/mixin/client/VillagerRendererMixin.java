package net.werdenrc5.villagerdance.mixin.client;

import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import net.werdenrc5.villagerdance.data.DanceDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(VillagerRenderer.class)
public class VillagerRendererMixin {
    
    private static final ResourceLocation DANCING_TEXTURE = 
        new ResourceLocation(VillagerDanceMod.MOD_ID, "textures/entity/villager/villager_dancing.png");
    
@Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
private void getDanceTexture(Villager villager, CallbackInfoReturnable<ResourceLocation> cir) {
    DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
    
    if (data != null && data.isDancing && data.danceTime > 0) {
        cir.setReturnValue(DANCING_TEXTURE);
    }
}
}