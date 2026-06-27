package cn.quit5700.pathfindingbeacon.registry;

import cn.quit5700.pathfindingbeacon.PathfindingBeaconMod;
import cn.quit5700.pathfindingbeacon.item.CancellerItem;
import cn.quit5700.pathfindingbeacon.item.SequenceReordererItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public final class ModItems {
    public static final List<BlockItem> ROUTE_BLOCK_ITEMS = registerBlockItems();
    public static final Item CANCELLER = Registry.register(
            Registries.ITEM,
            PathfindingBeaconMod.id("pathfinding_block_canceller"),
            new CancellerItem(new Item.Settings().maxCount(1))
    );
    public static final Item SEQUENCE_REORDERER = Registry.register(
            Registries.ITEM,
            PathfindingBeaconMod.id("id_sequence_reorderer"),
            new SequenceReordererItem(new Item.Settings().maxCount(1))
    );

    private ModItems() {
    }

    private static List<BlockItem> registerBlockItems() {
        List<BlockItem> result = new ArrayList<>(30);
        for (int i = 0; i < ModBlocks.ROUTE_BLOCKS.size(); i++) {
            BlockItem item = new BlockItem(ModBlocks.ROUTE_BLOCKS.get(i), new Item.Settings());
            Registry.register(Registries.ITEM, PathfindingBeaconMod.id("route_block_" + (i + 1)), item);
            result.add(item);
        }
        return List.copyOf(result);
    }

    public static void initialize() {
    }
}
