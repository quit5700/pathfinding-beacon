package cn.quit5700.pathfindingbeacon.item;

import cn.quit5700.pathfindingbeacon.block.PathfindingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class CancellerItem extends Item {
    public CancellerItem(Settings settings) {
        super(settings);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return state.getBlock() instanceof PathfindingBlock;
    }
}
