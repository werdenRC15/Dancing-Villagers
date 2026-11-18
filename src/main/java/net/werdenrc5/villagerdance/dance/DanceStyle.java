package net.werdenrc5.villagerdance.dance;

import net.minecraft.util.RandomSource;

public enum DanceStyle {
    SWAY,
    PIGLIN,
    METAL,
    TWIRL,
    JIG,
    FEVER,
    GLITCH,
    JELLY,
    CANCAN,
    SHIVER,
    PARROT;

    private static final DanceStyle[] VALUES = values();

    public static DanceStyle byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : SWAY;
    }

    public static int randomId(RandomSource random, int exclude, boolean allowJump) {
        int id;
        do {
            id = random.nextInt(VALUES.length);
        } while (id == exclude || (!allowJump && VALUES[id] == JIG));
        return id;
    }
}