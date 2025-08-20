package net.werdenrc5.villagerdance.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.werdenrc5.villagerdance.VillagerDanceMod;
import net.werdenrc5.villagerdance.data.DanceDataManager;

import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(VillagerDanceMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, DanceSyncPacket.class, 
            DanceSyncPacket::encode, DanceSyncPacket::decode, DanceSyncPacket::handle);
    }
    

    
public static void syncDanceData(Villager villager, DanceDataManager.DanceData data) {
    INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> villager), 
        new DanceSyncPacket(villager.getId(), data.danceTime, data.danceSpeed, data.isDancing, data.isPiglinDance));
}

public static class DanceSyncPacket {
    private final int entityId;
    private final int danceTime;
    private final float danceSpeed;
    private final boolean isDancing;
    private final boolean isPiglinDance;
    
    public DanceSyncPacket(int entityId, int danceTime, float danceSpeed, boolean isDancing, boolean isPiglinDance) {
        this.entityId = entityId;
        this.danceTime = danceTime;
        this.danceSpeed = danceSpeed;
        this.isDancing = isDancing;
        this.isPiglinDance = isPiglinDance;
    }
    
    public static void encode(DanceSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
        buffer.writeInt(packet.danceTime);
        buffer.writeFloat(packet.danceSpeed);
        buffer.writeBoolean(packet.isDancing);
        buffer.writeBoolean(packet.isPiglinDance);
    }
    
    public static DanceSyncPacket decode(FriendlyByteBuf buffer) {
        return new DanceSyncPacket(
            buffer.readInt(), 
            buffer.readInt(), 
            buffer.readFloat(), 
            buffer.readBoolean(),
            buffer.readBoolean()
        );
    }

        public static void handle(DanceSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (ctx.get().getDirection().getReceptionSide().isClient()) {
                    handleClientSide(packet);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    
    private static void handleClientSide(DanceSyncPacket packet) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level != null) {
            Entity entity = minecraft.level.getEntity(packet.entityId);
            if (entity instanceof Villager villager) {
                DanceDataManager.DanceData data = DanceDataManager.getDanceData(villager);
                data.danceTime = packet.danceTime;
                data.danceSpeed = packet.danceSpeed;
                data.isDancing = packet.isDancing;
                data.isPiglinDance = packet.isPiglinDance;
            }
        }
    }
}
}