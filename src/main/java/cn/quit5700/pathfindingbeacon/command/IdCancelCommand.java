package cn.quit5700.pathfindingbeacon.command;

import cn.quit5700.pathfindingbeacon.route.WorldRouteManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class IdCancelCommand {
    private static final String COMMAND = "idcancel";

    private IdCancelCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal(COMMAND)
                        .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                        .then(argument("number", IntegerArgumentType.integer(1, 30))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    int number = IntegerArgumentType.getInteger(context, "number");
                                    int removed = WorldRouteManager.clearColor(player.getServerWorld(), number);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("已删除本维度" + number + "号寻路方块：" + removed + "个"),
                                            false
                                    );
                                    return removed;
                                }))));
    }
}
