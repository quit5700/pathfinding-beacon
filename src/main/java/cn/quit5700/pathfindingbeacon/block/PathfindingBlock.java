package cn.quit5700.pathfindingbeacon.block;

import cn.quit5700.pathfindingbeacon.registry.ModItems;
import cn.quit5700.pathfindingbeacon.route.PlacementStatus;
import cn.quit5700.pathfindingbeacon.route.WorldRouteManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class PathfindingBlock extends Block {
    private final int number;

    public PathfindingBlock(int number) {
        super(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)
                .luminance(state -> 15)
                .strength(-1.0F, 3_600_000.0F)
                .pistonBehavior(PistonBehavior.BLOCK));
        this.number = number;
    }

    public int number() {
        return number;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        if (context.getWorld() instanceof ServerWorld world
                && WorldRouteManager.hasNumberAtColumn(world, number, context.getBlockPos().getX(), context.getBlockPos().getZ())) {
            if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(Text.literal("已有同号码方块，不得放置").styled(style -> style.withColor(0xFF5555)), true);
            }
            return null;
        }
        return getDefaultState();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world instanceof ServerWorld serverWorld && placer instanceof PlayerEntity player) {
            PlacementStatus status = WorldRouteManager.place(serverWorld, number, player.getUuid(), pos).status();
            if (status == PlacementStatus.INACTIVE) {
                player.sendMessage(Text.literal("其他玩家已使用这个颜色，该放置没有作用，请破坏")
                        .styled(style -> style.withColor(0xFF5555)), true);
            }
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.isCreative()) {
            return 1.0F;
        }
        if (player.getMainHandStack().isOf(ModItems.CANCELLER)) {
            if (!(world instanceof ServerWorld serverWorld)) {
                return 0.34F;
            }
            return WorldRouteManager.canRemove(serverWorld, player.getUuid(), pos, false) ? 0.34F : 0.0F;
        }
        return 0.0F;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            dropStack(world, pos, new ItemStack(this));
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world instanceof ServerWorld serverWorld) {
            WorldRouteManager.remove(serverWorld, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
