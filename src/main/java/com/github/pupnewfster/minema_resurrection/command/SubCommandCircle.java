package com.github.pupnewfster.minema_resurrection.command;

import com.github.pupnewfster.minema_resurrection.cam.path.PathHandler;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import com.github.pupnewfster.minema_resurrection.util.CamUtils;
import com.github.pupnewfster.minema_resurrection.util.Translations;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;

public class SubCommandCircle {

    private static final SimpleCommandExceptionType PATH_IS_POPULATED = new SimpleCommandExceptionType(Translations.COMMAND_PATH_IS_POPULATED.translate());
    private static final double sqrt2_2 = Math.sqrt(2) / 2;

    private static final Vec3 rootPoint = new Vec3(1, 0, 0);

    private static final Vec3[] rightMovingCircle = {rootPoint, new Vec3(sqrt2_2, 0, -sqrt2_2),
                                                     new Vec3(0, 0, -1), new Vec3(-sqrt2_2, 0, -sqrt2_2), new Vec3(-1, 0, 0),
                                                     new Vec3(-sqrt2_2, 0, sqrt2_2), new Vec3(0, 0, 1), new Vec3(sqrt2_2, 0, sqrt2_2)};

    private static final Vec3[] leftMovingCircle = {rootPoint, new Vec3(sqrt2_2, 0, sqrt2_2),
                                                    new Vec3(0, 0, 1), new Vec3(-sqrt2_2, 0, sqrt2_2), new Vec3(-1, 0, 0),
                                                    new Vec3(-sqrt2_2, 0, -sqrt2_2), new Vec3(0, 0, -1), new Vec3(sqrt2_2, 0, -sqrt2_2)};

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("circle")
              .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0))
                    .then(Commands.argument("circles", IntegerArgumentType.integer(1))
                          .then(Commands.literal("left")
                                .executes(ctx -> circle(ctx, leftMovingCircle))
                          ).then(Commands.literal("right")
                                .executes(ctx -> circle(ctx, rightMovingCircle))
                          )
                    )
              );
    }

    private static int circle(CommandContext<CommandSourceStack> ctx, Vec3[] circle) throws CommandSyntaxException {
        if (PathHandler.getWaypointSize() != 0) {
            throw PATH_IS_POPULATED.create();
        }
        CommandSourceStack source = ctx.getSource();
        double radius = DoubleArgumentType.getDouble(ctx, "radius");
        int circles = IntegerArgumentType.getInteger(ctx, "circles");
        Position playerPos = CamUtils.getPosition(CommandMinema.getPlayerOrException(source));
        for (int i = 0; i < circles; i++) {
            for (Vec3 point : circle) {
                PathHandler.addWaypoint(new Position(playerPos.x + point.x * radius, playerPos.y, playerPos.z + point.z * radius,
                      playerPos.pitch, playerPos.yaw, playerPos.roll, playerPos.fov));
            }
        }
        PathHandler.addWaypoint(new Position(playerPos.x + rootPoint.x * radius, playerPos.y, playerPos.z + rootPoint.z * radius, playerPos.pitch, playerPos.yaw,
              playerPos.roll, playerPos.fov));
        source.sendSuccess(Translations.COMMAND_PATH_CIRCLE_CREATED::translate, true);
        return circles;
    }
}