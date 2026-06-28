package cn.quit5700.pathfindingbeacon.registry;

import cn.quit5700.pathfindingbeacon.PathfindingBeaconMod;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroup {
    public static final CreativeModeTab PATHFINDING = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            PathfindingBeaconMod.id("pathfinding"),
            FabricCreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pathfinding_beacon.pathfinding"))
                    .icon(() -> new ItemStack(ModItems.ROUTE_BLOCK_ITEMS.get(0)))
                    .displayItems((context, entries) -> {
                        ModItems.ROUTE_BLOCK_ITEMS.forEach(entries::accept);
                        entries.accept(ModItems.CANCELLER);
                        entries.accept(ModItems.SEQUENCE_REORDERER);
                    })
                    .build()
    );

    private ModItemGroup() {
    }

    public static void initialize() {
    }
}
