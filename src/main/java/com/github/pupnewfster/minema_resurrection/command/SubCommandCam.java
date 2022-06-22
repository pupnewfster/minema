package com.github.pupnewfster.minema_resurrection.command;

import com.github.pupnewfster.minema_resurrection.cam.path.PathHandler;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import com.github.pupnewfster.minema_resurrection.util.CamUtils;
import com.github.pupnewfster.minema_resurrection.util.Translations;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SubCommandCam {

    private static final SimpleCommandExceptionType PATH_DOES_NOT_EXIST = new SimpleCommandExceptionType(Translations.COMMAND_PATH_DOES_NOT_EXIST.translate());
    static final SimpleCommandExceptionType PATH_IS_EMPTY = new SimpleCommandExceptionType(Translations.COMMAND_PATH_IS_EMPTY.translate());

    private SubCommandCam() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("cam")
              .then(SubCommandStart.register())
              .then(registerStop())
              .then(registerGoto())
              .then(registerInsert())
              .then(registerRemove())
              .then(registerReplace())
              .then(registerUndo())
              .then(registerClear())
              .then(SubCommandExportImport.registerSave())
              .then(SubCommandExportImport.registerLoad())
              .then(registerTarget())
              .then(SubCommandCircle.register())
              .then(registerPreview())
              ;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerInsert() {
        return registerPointCommand("insert", "beforePoint", Translations.COMMAND_PATH_POINT_INSERTED,
              (source, index) -> PathHandler.insert(CamUtils.getPosition(CommandMinema.getPlayerOrException(source)), index));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerRemove() {
        return registerPointCommand("remove", "point", Translations.COMMAND_PATH_POINT_REMOVED, (source, index) -> PathHandler.remove(index));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerReplace() {
        return registerPointCommand("replace", "point", Translations.COMMAND_PATH_REPLACE,
              (source, index) -> PathHandler.replace(CamUtils.getPosition(CommandMinema.getPlayerOrException(source)), index));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerGoto() {
        return registerPointCommand("goto", "point", Translations.COMMAND_TRAVELLED_TO, (source, index) -> {
            Position pos = PathHandler.getWaypoint(index);
            if (pos == null) {
                return false;
            }
            CamUtils.teleport(CommandMinema.getPlayerOrException(source), pos, true);
            return true;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerPointCommand(String name, String pointArgName, Translations successMessage, PointTest test) {
        return Commands.literal(name)
              .then(Commands.argument(pointArgName, IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        CommandSourceStack source = ctx.getSource();
                        int index = IntegerArgumentType.getInteger(ctx, pointArgName);
                        if (test.test(source, index - 1)) {
                            source.sendSuccess(successMessage.translate(index), true);
                            return index;
                        }
                        throw PATH_DOES_NOT_EXIST.create();
                    })
              );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerClear() {
        return Commands.literal("clear")
              .executes(ctx -> {
                  CommandSourceStack source = ctx.getSource();
                  int waypointsCleared = PathHandler.clearWaypoints();
                  source.sendSuccess(Translations.COMMAND_PATH_RESET.translate(), true);
                  if (PathHandler.hasTarget()) {
                      source.sendSuccess(Translations.COMMAND_PATH_RESET_BEWARE_TARGET.translate(), true);
                  }
                  return waypointsCleared;
              });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerPreview() {
        return Commands.literal("preview")
              .executes(ctx -> {
                  PathHandler.switchPreview();
                  if (PathHandler.showPreview()) {
                      ctx.getSource().sendSuccess(Translations.RENDER_PREVIEW_ON.translate(), true);
                      return 1;
                  }
                  ctx.getSource().sendSuccess(Translations.RENDER_PREVIEW_OFF.translate(), true);
                  return 0;
              });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerStop() {
        return Commands.literal("stop")
              .executes(ctx -> {
                  PathHandler.stopTravelling();
                  ctx.getSource().sendSuccess(Translations.COMMAND_PATH_STOPPED.translate(), true);
                  return 0;
              });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerTarget() {
        return Commands.literal("target")
              .then(Commands.literal("off")
                    .executes(ctx -> {
                        PathHandler.removeTarget();
                        ctx.getSource().sendSuccess(Translations.COMMAND_PATH_REMOVED_TARGET.translate(), true);
                        return 0;
                    })
              ).then(Commands.literal("set")
                    .executes(ctx -> {
                        CommandSourceStack source = ctx.getSource();
                        PathHandler.setTarget(CommandMinema.getPlayerOrException(source).position());
                        source.sendSuccess(Translations.COMMAND_PATH_SET_TARGET.translate(), true);
                        return 0;
                    })
              );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerUndo() {
        return Commands.literal("undo")
              .executes(ctx -> {
                  if (PathHandler.removeLastWaypoint()) {
                      ctx.getSource().sendSuccess(Translations.COMMAND_PATH_UNDO.translate(), true);
                      return PathHandler.getWaypointSize();
                  }
                  throw PATH_IS_EMPTY.create();
              });
    }

    private interface PointTest {

        boolean test(CommandSourceStack source, int index) throws CommandSyntaxException;
    }
}