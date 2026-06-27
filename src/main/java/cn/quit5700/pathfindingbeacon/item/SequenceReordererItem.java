package cn.quit5700.pathfindingbeacon.item;

import cn.quit5700.pathfindingbeacon.block.PathfindingBlock;
import cn.quit5700.pathfindingbeacon.route.ReorderStatus;
import cn.quit5700.pathfindingbeacon.route.RoutePosition;
import cn.quit5700.pathfindingbeacon.route.WorldRouteManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SequenceReordererItem extends Item {
    private static final Map<UUID, Selection> SELECTIONS = new HashMap<>();

    public SequenceReordererItem(Settings settings) {
        super(settings);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getWorld() instanceof ServerWorld world) || context.getPlayer() == null) {
            return ActionResult.SUCCESS;
        }
        if (!(world.getBlockState(context.getBlockPos()).getBlock() instanceof PathfindingBlock block)) {
            return ActionResult.PASS;
        }

        UUID playerId = context.getPlayer().getUuid();
        RoutePosition position = WorldRouteManager.toRoutePosition(context.getBlockPos());
        if (!WorldRouteManager.isActive(world, position)) {
            context.getPlayer().sendMessage(Text.literal("该方块未参与线路，无法重排").styled(style -> style.withColor(0xFF5555)), true);
            return ActionResult.FAIL;
        }

        Selection first = SELECTIONS.get(playerId);
        if (first == null || !first.worldKey().equals(world.getRegistryKey().getValue()) || first.number() != block.number()) {
            SELECTIONS.put(playerId, new Selection(world.getRegistryKey().getValue(), block.number(), position));
            context.getPlayer().sendMessage(Text.literal("已选择第一个" + block.number() + "号寻路方块"), true);
            return ActionResult.SUCCESS;
        }

        SELECTIONS.remove(playerId);
        ReorderStatus status = WorldRouteManager.reorder(
                world,
                block.number(),
                playerId,
                first.position(),
                position,
                context.getPlayer().isCreative()
        );
        Text message = switch (status) {
            case RECONNECTED -> Text.literal("两段线路已连接");
            case REORDERED -> Text.literal("线路顺序已重排");
            case DENIED -> Text.literal("其他玩家已使用这个颜色，该操作没有作用").styled(style -> style.withColor(0xFF5555));
            case INVALID -> Text.literal("请选择两个不同的同号有效方块").styled(style -> style.withColor(0xFF5555));
        };
        context.getPlayer().sendMessage(message, true);
        return status == ReorderStatus.DENIED || status == ReorderStatus.INVALID
                ? ActionResult.FAIL
                : ActionResult.SUCCESS;
    }

    private record Selection(net.minecraft.util.Identifier worldKey, int number, RoutePosition position) {
    }
}
