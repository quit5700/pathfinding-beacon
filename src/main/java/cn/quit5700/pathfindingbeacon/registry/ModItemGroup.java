package cn.quit5700.pathfindingbeacon.registry;

import cn.quit5700.pathfindingbeacon.PathfindingBeaconMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class ModItemGroup {
    public static final ItemGroup PATHFINDING = Registry.register(
            Registries.ITEM_GROUP,
            PathfindingBeaconMod.id("pathfinding"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.pathfinding_beacon.pathfinding"))
                    .icon(() -> new ItemStack(ModItems.ROUTE_BLOCK_ITEMS.get(0)))
                    .entries((context, entries) -> {
                        ModItems.ROUTE_BLOCK_ITEMS.forEach(entries::add);
                        entries.add(ModItems.CANCELLER);
                        entries.add(ModItems.SEQUENCE_REORDERER);
                    })
                    .build()
    );

    private ModItemGroup() {
    }

    public static void initialize() {
    }
}
