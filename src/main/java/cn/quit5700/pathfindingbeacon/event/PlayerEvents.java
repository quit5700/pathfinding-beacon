package cn.quit5700.pathfindingbeacon.event;

import cn.quit5700.pathfindingbeacon.PathfindingBeaconMod;
import cn.quit5700.pathfindingbeacon.network.RouteNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class PlayerEvents {
    private static int ticks;

    private PlayerEvents() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            handler.player.sendMessage(Text.literal("寻路器取消指令 idcancel <1-30>"), false);
            RouteNetworking.syncPlayer(handler.player);
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                RouteNetworking.syncPlayer(player));
        ServerTickEvents.END_SERVER_TICK.register(PlayerEvents::unlockRecipesForPickaxeOwners);
    }

    private static void unlockRecipesForPickaxeOwners(MinecraftServer server) {
        if (++ticks % 20 != 0) {
            return;
        }
        List<Identifier> ids = new ArrayList<>();
        ids.add(PathfindingBeaconMod.id("route_block_1"));
        for (int i = 2; i <= 30; i++) {
            ids.add(PathfindingBeaconMod.id("route_block_" + i));
        }
        ids.add(PathfindingBeaconMod.id("pathfinding_block_canceller"));
        ids.add(PathfindingBeaconMod.id("id_sequence_reorderer"));

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean hasPickaxe = player.getInventory().main.stream().anyMatch(PlayerEvents::isPickaxe);
            if (hasPickaxe) {
                player.unlockRecipes(ids);
            }
        }
    }

    private static boolean isPickaxe(ItemStack stack) {
        return stack.isIn(ItemTags.PICKAXES);
    }
}
