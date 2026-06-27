package cn.quit5700.pathfindingbeacon.route;

import cn.quit5700.pathfindingbeacon.network.RouteNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public final class WorldRouteManager {
    private WorldRouteManager() {
    }

    public static RoutePersistentState state(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(RoutePersistentState.TYPE, RoutePersistentState.ID);
    }

    public static PlacementResult place(ServerWorld world, int number, UUID player, BlockPos pos) {
        RoutePersistentState state = state(world);
        PlacementResult result = state.service().place(number, player, toRoutePosition(pos));
        state.markDirty();
        RouteNetworking.syncWorld(world);
        return result;
    }

    public static boolean hasNumberAtColumn(ServerWorld world, int number, int x, int z) {
        return state(world).data().nodes().stream().anyMatch(node ->
                node.number() == number && node.position().x() == x && node.position().z() == z);
    }

    public static boolean isActive(ServerWorld world, RoutePosition position) {
        RouteNode node = state(world).data().node(position);
        return node != null && node.active();
    }

    public static boolean canRemove(ServerWorld world, UUID player, BlockPos pos, boolean creative) {
        return state(world).service().canRemove(toRoutePosition(pos), player, creative);
    }

    public static void remove(ServerWorld world, BlockPos pos) {
        RoutePersistentState state = state(world);
        if (state.service().remove(toRoutePosition(pos)) != null) {
            state.markDirty();
            RouteNetworking.syncWorld(world);
        }
    }

    public static ReorderStatus reorder(
            ServerWorld world,
            int number,
            UUID player,
            RoutePosition first,
            RoutePosition second,
            boolean creative
    ) {
        RoutePersistentState state = state(world);
        ReorderStatus result = state.service().reorder(number, player, first, second, creative);
        if (result == ReorderStatus.RECONNECTED || result == ReorderStatus.REORDERED) {
            state.markDirty();
            RouteNetworking.syncWorld(world);
        }
        return result;
    }

    public static int clearColor(ServerWorld world, int number) {
        RoutePersistentState state = state(world);
        List<RouteNode> removed = state.service().clearColor(number);
        state.markDirty();
        for (RouteNode node : removed) {
            BlockPos pos = toBlockPos(node.position());
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
        RouteNetworking.syncWorld(world);
        return removed.size();
    }

    public static RoutePosition toRoutePosition(BlockPos pos) {
        return new RoutePosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos toBlockPos(RoutePosition pos) {
        return new BlockPos(pos.x(), pos.y(), pos.z());
    }
}
