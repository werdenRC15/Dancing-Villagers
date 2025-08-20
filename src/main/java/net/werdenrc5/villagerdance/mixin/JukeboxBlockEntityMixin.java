package net.werdenrc5.villagerdance.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.AABB;
import net.werdenrc5.villagerdance.config.DanceConfig;
import net.werdenrc5.villagerdance.data.DanceDataManager;
import net.werdenrc5.villagerdance.mixin.accessor.JukeboxBlockEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxBlockEntityMixin {
    
    @Inject(method = "startPlaying", at = @At("TAIL"))
    private void onStartPlaying(CallbackInfo ci) {
        if (!DanceConfig.SERVER.danceNearJukebox.get()) {
            return;
        }
        
        JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;
        
        if (self.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos pos = self.getBlockPos();
            double radius = DanceConfig.SERVER.danceRadius.get();
            
            AABB searchArea = new AABB(pos).inflate(radius);
            serverLevel.getEntitiesOfClass(Villager.class, searchArea).forEach(villager -> {
                if (!DanceConfig.SERVER.babiesCanDance.get() && villager.isBaby()) {
                    return;
                }
                
                DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
                if (!data.isDancing) {
                    DanceDataManager.startDancing(villager, Integer.MAX_VALUE, true);
                }
            });
        }
    }
    
@Inject(method = "stopPlaying", at = @At("TAIL"))
private void onStopPlaying(CallbackInfo ci) {
    if (!DanceConfig.SERVER.danceNearJukebox.get()) {
        return;
    }
    
    JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;
    
    if (self.getLevel() instanceof ServerLevel serverLevel) {
        BlockPos pos = self.getBlockPos();
        double radius = DanceConfig.SERVER.danceRadius.get();
        
        if (self.isEmpty()) {
            AABB searchArea = new AABB(pos).inflate(radius);
            serverLevel.getEntitiesOfClass(Villager.class, searchArea).forEach(villager -> {
                DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
                if (data.isDancing) {
                    boolean hasActiveJukebox = false;
                    for (BlockPos nearbyPos : BlockPos.betweenClosed(
                        pos.offset(-(int)radius, -(int)radius, -(int)radius),
                        pos.offset((int)radius, (int)radius, (int)radius))) {
                        
                        if (serverLevel.getBlockEntity(nearbyPos) instanceof JukeboxBlockEntity jukebox) {
                            if (((JukeboxBlockEntityAccessor) jukebox).isPlaying()) {
                                hasActiveJukebox = true;
                                break;
                            }
                        }
                    }
                    
                    if (!hasActiveJukebox) {
                        DanceDataManager.stopDancing(villager);
                    }
                }
            });
        }
    }
}


}