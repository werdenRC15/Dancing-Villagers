package net.werdenrc5.villagerdance.dance;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.werdenrc5.villagerdance.config.DanceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class DanceController {
    private static final int SCAN_INTERVAL = 2;
    private static final double PLAYER_LOOK_RANGE = 8.0;
    private static final Map<ServerLevel, Map<Long, Scan>> SCANS = new WeakHashMap<>();

    private record Scan(long time, List<BlockPos> jukeboxes) {
    }

    private DanceController() {
    }

    public static void tick(Villager villager) {
        if (!(villager.level() instanceof ServerLevel level)) return;

        IDancingVillager dancer = (IDancingVillager) villager;

        if (dancer.isDancing()) {
            tickDancing(level, villager, dancer);
            return;
        }

        if (!DanceConfig.DANCE_NEAR_JUKEBOX.get()) return;
        if ((villager.tickCount + villager.getId()) % SCAN_INTERVAL != 0) return;
        if (!canDance(villager)) return;

        BlockPos jukebox = findPlayingJukebox(level, villager.blockPosition());
        if (jukebox != null) {
            start(villager, durationTicks(), jukebox);
        }
    }

    public static void celebrate(Villager villager) {
        IDancingVillager dancer = (IDancingVillager) villager;
        if (dancer.isDancing()) return;
        if (canDance(villager)) {
            start(villager, durationTicks() * 2, null);
        }
    }

    private static void tickDancing(ServerLevel level, Villager villager, IDancingVillager dancer) {
        if (!canDance(villager)) {
            dancer.stopDance();
            return;
        }

        villager.getNavigation().stop();
        villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        Vec3 motion = villager.getDeltaMovement();
        villager.setDeltaMovement(0.0, motion.y, 0.0);

        BlockPos jukebox = dancer.getDanceJukebox();
        int remaining = dancer.getDanceTicks() - 1;
        boolean finished = jukebox != null ? !isPlayingNear(level, jukebox, villager) : remaining <= 0;

        if (finished) {
            dancer.stopDance();
            return;
        }

        if (remaining <= 0 && jukebox != null) {
            start(villager, durationTicks(), jukebox);
        } else {
            dancer.setDanceTicks(Math.max(remaining, 0));
        }

        Player player = level.getNearestPlayer(villager, PLAYER_LOOK_RANGE);
        if (player != null && !player.isSpectator()) {
            villager.getLookControl().setLookAt(player, 30.0F, 30.0F);
        } else if (jukebox != null) {
            villager.getLookControl().setLookAt(jukebox.getX() + 0.5, jukebox.getY() + 0.5, jukebox.getZ() + 0.5);
        }

        DanceEffects.tick(villager, level, jukebox != null);
    }

    private static void start(Villager villager, int ticks, BlockPos jukebox) {
        IDancingVillager dancer = (IDancingVillager) villager;

        int animation = DanceStyle.randomId(villager.getRandom(), dancer.getDanceAnimation(), jukebox != null);

        float speed = 1.0F;
        if (DanceConfig.RANDOMIZE_SPEEDS.get()) {
            float a = DanceConfig.MIN_SPEED.get().floatValue();
            float b = DanceConfig.MAX_SPEED.get().floatValue();
            float min = Math.min(a, b);
            float max = Math.max(a, b);
            speed = min + villager.getRandom().nextFloat() * (max - min);
        }

        dancer.startDance(animation, speed, ticks, jukebox);
        villager.getNavigation().stop();
    }

    private static boolean canDance(Villager villager) {
        if (!villager.isAlive() || villager.isSleeping()) return false;
        if (villager.getTradingPlayer() != null) return false;
        if (villager.hurtTime > 0) return false;

        Brain<Villager> brain = villager.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE)
            || brain.hasMemoryValue(MemoryModuleType.HURT_BY)
            || brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY)
            || brain.isActive(Activity.PANIC)) {
            return false;
        }

        return DanceConfig.BABIES_CAN_DANCE.get() || !villager.isBaby();
    }

    private static int durationTicks() {
        return DanceConfig.DANCE_SECONDS.get() * 20;
    }

    private static boolean isPlayingNear(ServerLevel level, BlockPos pos, Villager villager) {
        if (!level.isLoaded(pos)) return false;
        if (!(level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox) || !jukebox.isRecordPlaying()) return false;
        double limit = DanceConfig.DANCE_RADIUS.get() + 2.0;
        return villager.blockPosition().distSqr(pos) <= limit * limit;
    }

    private static BlockPos findPlayingJukebox(ServerLevel level, BlockPos origin) {
        int radius = DanceConfig.DANCE_RADIUS.get();
        double limit = (double) radius * radius;

        List<BlockPos> candidates = scan(level, new ChunkPos(origin), radius);

        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : candidates) {
            double distance = pos.distSqr(origin);
            if (distance <= limit && distance < bestDistance) {
                bestDistance = distance;
                best = pos;
            }
        }
        return best;
    }

    private static List<BlockPos> scan(ServerLevel level, ChunkPos center, int radius) {
        Map<Long, Scan> cache = SCANS.computeIfAbsent(level, l -> new HashMap<>());
        long now = level.getGameTime();
        Scan cached = cache.get(center.toLong());
        if (cached != null && now - cached.time() < SCAN_INTERVAL) {
            return cached.jukeboxes();
        }

        if (cache.size() > 256) {
            cache.values().removeIf(s -> now - s.time() >= SCAN_INTERVAL);
        }

        int range = (radius >> 4) + 1;
        List<BlockPos> found = new ArrayList<>();
        for (int cx = center.x - range; cx <= center.x + range; cx++) {
            for (int cz = center.z - range; cz <= center.z + range; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) continue;
                for (BlockEntity entity : chunk.getBlockEntities().values()) {
                    if (entity instanceof JukeboxBlockEntity jukebox && jukebox.isRecordPlaying()) {
                        found.add(entity.getBlockPos());
                    }
                }
            }
        }

        cache.put(center.toLong(), new Scan(now, found));
        return found;
    }
}