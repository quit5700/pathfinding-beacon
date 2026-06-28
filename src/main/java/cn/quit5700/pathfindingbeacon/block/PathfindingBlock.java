package cn.quit5700.pathfindingbeacon.block;

import cn.quit5700.pathfindingbeacon.registry.ModItems;
import cn.quit5700.pathfindingbeacon.route.PlacementStatus;
import cn.quit5700.pathfindingbeacon.route.WorldRouteManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public final class PathfindingBlock extends Block {
    private final int number;

    public PathfindingBlock(int number) {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE)
                .lightLevel(state -> 15)
                .strength(-1.0F, 3_600_000.0F)
                .pushReaction(PushReaction.BLOCK));
        this.number = number;
    }

    public int number() {
        return number;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getLevel() instanceof ServerLevel world
                && WorldRouteManager.hasNumberAtColumn(world, number, context.getClickedPos().getX(), context.getClickedPos().getZ())) {
            if (context.getPlayer() != null) {
                context.getPlayer().sendOverlayMessage(
                        Component.literal("已有同号码方块,不得放置").withStyle(style -> style.withColor(0xFF5555))
                );
            }
            return null;
        }
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (world instanceof ServerLevel serverWorld && placer instanceof Player player) {
            PlacementStatus status = WorldRouteManager.place(serverWorld, number, player.getUUID(), pos).status();
            if (status == PlacementStatus.INACTIVE) {
                player.sendOverlayMessage(
                        Component.literal("其他玩家已使用这个颜色,该放置没有作用,请破坏.")
                                .withStyle(style -> style.withColor(0xFF5555))
                );
            }
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        if (player.isCreative()) {
            return 1.0F;
        }
        if (player.getMainHandItem().is(ModItems.CANCELLER)) {
            if (!(world instanceof ServerLevel serverWorld)) {
                return 0.34F;
            }
            return WorldRouteManager.canRemove(serverWorld, player.getUUID(), pos, false) ? 0.34F : 0.0F;
        }
        return 0.0F;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide() && player.isCreative()) {
            popResource(world, pos, new ItemStack(this));
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        WorldRouteManager.remove(world, pos);
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }
}
