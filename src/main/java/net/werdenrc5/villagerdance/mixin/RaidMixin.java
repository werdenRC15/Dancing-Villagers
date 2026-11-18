package net.werdenrc5.villagerdance.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.phys.AABB;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.dance.DanceController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    public abstract boolean isVictory();

    @Shadow
    public abstract BlockPos getCenter();

    @Unique
    private int villagerdance$timer;

    @Inject(method = "tick", at = @At("TAIL"))
    private void villagerdance$tick(CallbackInfo ci) {
        if (!this.isVictory()) return;
        if (this.villagerdance$timer++ % 20 != 0) return;
        if (!DanceConfig.DANCE_AFTER_RAID.get()) return;

        AABB area = new AABB(this.getCenter()).inflate(DanceConfig.RAID_RADIUS.get());
        for (Villager villager : this.level.getEntitiesOfClass(Villager.class, area)) {
            DanceController.celebrate(villager);
        }
    }
}