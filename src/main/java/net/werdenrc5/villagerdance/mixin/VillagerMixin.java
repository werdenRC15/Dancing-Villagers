package net.werdenrc5.villagerdance.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.level.Level;
import net.werdenrc5.villagerdance.dance.DanceController;
import net.werdenrc5.villagerdance.dance.IDancingVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder, IDancingVillager {
    @Unique
    private static final EntityDataAccessor<Integer> VILLAGERDANCE_ANIMATION =
        SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<Float> VILLAGERDANCE_SPEED =
        SynchedEntityData.defineId(Villager.class, EntityDataSerializers.FLOAT);

    @Unique
    private static final EntityDataAccessor<Integer> VILLAGERDANCE_VARIANT =
        SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);

    @Unique
    private static final float VILLAGERDANCE_BLEND_TICKS = 12.0F;

    @Unique
    private int villagerdance$ticks;

    @Unique
    private int villagerdance$delay;

    @Unique
    private BlockPos villagerdance$jukebox;

    @Unique
    private int villagerdance$lastAnimation = -1;

    @Unique
    private float villagerdance$lastSpeed = 1.0F;

    @Unique
    private int villagerdance$previousAnimation = -1;

    @Unique
    private float villagerdance$previousSpeed = 1.0F;

    @Unique
    private float villagerdance$switchTick = -1000.0F;

    @Unique
    private float villagerdance$prevProgress;

    @Unique
    private float villagerdance$progress;

    protected VillagerMixin(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void villagerdance$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(VILLAGERDANCE_ANIMATION, -1);
        this.entityData.define(VILLAGERDANCE_SPEED, 1.0F);
        this.entityData.define(VILLAGERDANCE_VARIANT, 0);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void villagerdance$tick(CallbackInfo ci) {
        if (!this.level().isClientSide) return;
        this.villagerdance$prevProgress = this.villagerdance$progress;
        if (this.isDancing()) {
            if (this.villagerdance$progress < 5.0F) this.villagerdance$progress++;
        } else if (this.villagerdance$progress > 0.0F) {
            this.villagerdance$progress--;
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void villagerdance$customServerAiStep(CallbackInfo ci) {
        DanceController.tick((Villager) (Object) this);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);

        if (VILLAGERDANCE_ANIMATION.equals(accessor)) {
            int animation = this.entityData.get(VILLAGERDANCE_ANIMATION);
            if (animation != this.villagerdance$lastAnimation) {
                this.villagerdance$previousAnimation = this.villagerdance$lastAnimation;
                this.villagerdance$previousSpeed = this.villagerdance$lastSpeed;
                this.villagerdance$switchTick = this.tickCount;
                this.villagerdance$lastAnimation = animation;
            }
        } else if (VILLAGERDANCE_SPEED.equals(accessor)) {
            this.villagerdance$lastSpeed = this.entityData.get(VILLAGERDANCE_SPEED);
        }
    }

    @Override
    public boolean isDancing() {
        return this.entityData.get(VILLAGERDANCE_ANIMATION) >= 0;
    }

    @Override
    public int getDanceAnimation() {
        return this.entityData.get(VILLAGERDANCE_ANIMATION);
    }

    @Override
    public int getDanceVariant() {
        return this.entityData.get(VILLAGERDANCE_VARIANT);
    }

    @Override
    public float getDanceSpeed() {
        return this.entityData.get(VILLAGERDANCE_SPEED);
    }

    @Override
    public int getPreviousDanceAnimation() {
        return this.villagerdance$previousAnimation;
    }

    @Override
    public float getPreviousDanceSpeed() {
        return this.villagerdance$previousSpeed;
    }

    @Override
    public float getDanceBlend(float ageInTicks) {
        float b = Mth.clamp((ageInTicks - this.villagerdance$switchTick) / VILLAGERDANCE_BLEND_TICKS, 0.0F, 1.0F);
        return b * b * (3.0F - 2.0F * b);
    }

    @Override
    public float getDanceProgress(float partialTick) {
        float p = (this.villagerdance$prevProgress
            + (this.villagerdance$progress - this.villagerdance$prevProgress) * partialTick) * 0.2F;
        return p * p * (3.0F - 2.0F * p);
    }

    @Override
    public int getDanceTicks() {
        return this.villagerdance$ticks;
    }

    @Override
    public void setDanceTicks(int ticks) {
        this.villagerdance$ticks = ticks;
    }

    @Override
    public int getDanceDelay() {
        return this.villagerdance$delay;
    }

    @Override
    public void setDanceDelay(int ticks) {
        this.villagerdance$delay = ticks;
    }

    @Override
    public BlockPos getDanceJukebox() {
        return this.villagerdance$jukebox;
    }

    @Override
    public void setDanceJukebox(BlockPos jukebox) {
        this.villagerdance$jukebox = jukebox;
    }

    @Override
    public void startDance(int animation, float speed, int ticks, BlockPos jukebox) {
        if (!this.isDancing()) {
            this.entityData.set(VILLAGERDANCE_VARIANT, this.random.nextInt(2));
        }
        this.entityData.set(VILLAGERDANCE_ANIMATION, animation);
        this.entityData.set(VILLAGERDANCE_SPEED, speed);
        this.villagerdance$ticks = ticks;
        this.villagerdance$delay = 0;
        this.villagerdance$jukebox = jukebox;
    }

    @Override
    public void stopDance() {
        this.entityData.set(VILLAGERDANCE_ANIMATION, -1);
        this.villagerdance$ticks = 0;
        this.villagerdance$delay = 0;
        this.villagerdance$jukebox = null;
    }
}