package com.github.pupnewfster.minema_resurrection.command;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;

public class CommandMinema {

    private CommandMinema() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("minema")
              .then(registerEnable())
              .then(registerDisable())
              .then(SubCommandCam.register());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerEnable() {
        return Commands.literal("enable").executes(ctx -> CaptureSession.singleton.startCapture() ? 1 : 0);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerDisable() {
        return Commands.literal("disable").executes(ctx -> CaptureSession.singleton.stopCapture() ? 1 : 0);
    }

    static LocalPlayer getPlayerOrException(CommandSourceStack source) throws CommandSyntaxException {
        Entity entity = source.getEntity();
        if (entity instanceof LocalPlayer player) {
            return player;
        }
        throw CommandSourceStack.ERROR_NOT_PLAYER.create();
    }
}