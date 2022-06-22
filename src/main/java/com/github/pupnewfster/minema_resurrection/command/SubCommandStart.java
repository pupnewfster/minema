package com.github.pupnewfster.minema_resurrection.command;

import com.github.pupnewfster.minema_resurrection.cam.path.PathHandler;
import com.github.pupnewfster.minema_resurrection.util.Translations;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SubCommandStart {

    private static final SimpleCommandExceptionType COMMAND_AT_LEAST_TWO_POINTS = new SimpleCommandExceptionType(Translations.COMMAND_AT_LEAST_TWO_POINTS.translate());

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("start")
              .then(Commands.argument("frames", LongArgumentType.longArg(1))
                    .executes(ctx -> start(ctx, false))
                    .then(Commands.argument("record", BoolArgumentType.bool())
                          .executes(ctx -> start(ctx, BoolArgumentType.getBool(ctx, "record")))
                    )
              );
    }

    private static int start(CommandContext<CommandSourceStack> ctx, boolean record) throws CommandSyntaxException {
        if (PathHandler.getWaypointSize() < 2) {
            throw COMMAND_AT_LEAST_TWO_POINTS.create();
        }
        long frames = LongArgumentType.getLong(ctx, "frames");
        CommandSourceStack source = ctx.getSource();
        PathHandler.startTravelling(CommandMinema.getPlayerOrException(source), frames, record);
        source.sendSuccess(Translations.COMMAND_PATH_STARTED.translate(), true);
        return 0;
    }
}