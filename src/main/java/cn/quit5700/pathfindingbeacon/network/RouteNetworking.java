package cn.quit5700.pathfindingbeacon.network;

import cn.quit5700.pathfindingbeacon.PathfindingBeaconMod;
import cn.quit5700.pathfindingbeacon.route.RouteEdge;
import cn.quit5700.pathfindingbeacon.route.RouteNode;
import cn.quit5700.pathfindingbeacon.route.RoutePosition;
import cn.quit5700.pathfindingbeacon.route.RoutePersistentState;
import cn.quit5700.pathfindingbeacon.route.WorldRouteManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RouteNetworking {
    public static final Identifier ROUTE_SNAPSHOT = PathfindingBeaconMod.id("route_snapshot");
    private static final UUID SERVER_OWNER = new UUID(0L, 0L);

    private RouteNetworking() {
    }

    public static void registerServer() {
        PayloadTypeRegistry.playS2C().register(RouteSnapshotPayload.ID, RouteSnapshotPayload.CODEC);
    }

    public static void syncWorld(ServerWorld world) {
        for (ServerPlayerEntity player : PlayerLookup.world(world)) {
            syncPlayer(player);
        }
    }

    public static void syncPlayer(ServerPlayerEntity player) {
        RoutePersistentState state = WorldRouteManager.state(player.getServerWorld());
        ServerPlayNetworking.send(player, new RouteSnapshotPayload(
                List.copyOf(state.data().nodes()),
                List.copyOf(state.data().allEdges())
        ));
    }

    public record RouteSnapshotPayload(List<RouteNode> nodes, List<RouteEdge> edges) implements CustomPayload {
        public static final CustomPayload.Id<RouteSnapshotPayload> ID = new CustomPayload.Id<>(ROUTE_SNAPSHOT);
        public static final PacketCodec<RegistryByteBuf, RouteSnapshotPayload> CODEC = CustomPayload.codecOf(
                RouteSnapshotPayload::write,
                RouteSnapshotPayload::read
        );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }

        private void write(RegistryByteBuf buf) {
            buf.writeVarInt(nodes.size());
            for (RouteNode node : nodes) {
                buf.writeVarInt(node.number());
                buf.writeBlockPos(WorldRouteManager.toBlockPos(node.position()));
                buf.writeBoolean(node.active());
            }
            buf.writeVarInt(edges.size());
            for (RouteEdge edge : edges) {
                buf.writeVarInt(edge.number());
                buf.writeBlockPos(WorldRouteManager.toBlockPos(edge.first()));
                buf.writeBlockPos(WorldRouteManager.toBlockPos(edge.second()));
                buf.writeLong(edge.createdOrder());
            }
        }

        private static RouteSnapshotPayload read(RegistryByteBuf buf) {
            int nodeCount = buf.readVarInt();
            List<RouteNode> nodes = new ArrayList<>(nodeCount);
            for (int i = 0; i < nodeCount; i++) {
                int number = buf.readVarInt();
                BlockPos pos = buf.readBlockPos();
                boolean active = buf.readBoolean();
                nodes.add(new RouteNode(number, SERVER_OWNER, toRoutePosition(pos), active));
            }
            int edgeCount = buf.readVarInt();
            List<RouteEdge> edges = new ArrayList<>(edgeCount);
            for (int i = 0; i < edgeCount; i++) {
                int number = buf.readVarInt();
                RoutePosition first = toRoutePosition(buf.readBlockPos());
                RoutePosition second = toRoutePosition(buf.readBlockPos());
                long createdOrder = buf.readLong();
                edges.add(new RouteEdge(number, first, second, createdOrder));
            }
            return new RouteSnapshotPayload(nodes, edges);
        }

        private static RoutePosition toRoutePosition(BlockPos pos) {
            return new RoutePosition(pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
