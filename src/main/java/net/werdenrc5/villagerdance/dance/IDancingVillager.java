package net.werdenrc5.villagerdance.dance;

import net.minecraft.core.BlockPos;

public interface IDancingVillager {
    boolean isDancing();

    int getDanceAnimation();

    int getDanceVariant();

    float getDanceSpeed();

    int getPreviousDanceAnimation();

    float getPreviousDanceSpeed();

    float getDanceBlend(float ageInTicks);

    float getDanceProgress(float partialTick);

    int getDanceTicks();

    void setDanceTicks(int ticks);

    int getDanceDelay();

    void setDanceDelay(int ticks);

    BlockPos getDanceJukebox();

    void setDanceJukebox(BlockPos jukebox);

    void startDance(int animation, float speed, int ticks, BlockPos jukebox);

    void stopDance();
}