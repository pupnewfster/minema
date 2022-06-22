package com.github.pupnewfster.minema_resurrection.command;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.cam.path.PathHandler;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import com.github.pupnewfster.minema_resurrection.util.Translations;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SubCommandExportImport {

    private static final SimpleCommandExceptionType FILE_ALREADY_EXISTS = new SimpleCommandExceptionType(Translations.EXPORTER_FILE_ALREADY_EXISTS.translate());
    private static final SimpleCommandExceptionType FILE_DOES_NOT_EXIST = new SimpleCommandExceptionType(Translations.EXPORTER_FILE_DOES_NOT_EXIST.translate());
    private static final SimpleCommandExceptionType INVALID_POINT = new SimpleCommandExceptionType(Translations.COMMAND_PATH_INVALID_POINT.translate());

    private static final String extension = ".txt";

    static ArgumentBuilder<CommandSourceStack, ?> registerLoad() {
        return Commands.literal("load")
              .then(Commands.argument("fileName", StringArgumentType.word())
                    .executes(ctx -> {
                        String fileName = StringArgumentType.getString(ctx, "fileName");
                        File file = MinemaResurrection.getCameraDirectory().resolve(fileName + extension).toFile();
                        if (!file.isFile()) {
                            throw FILE_DOES_NOT_EXIST.create();
                        }
                        CommandSourceStack source = ctx.getSource();
                        List<Position> points = new ArrayList<>();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                            String s;
                            while ((s = reader.readLine()) != null) {
                                Position point = Position.fromString(s, source::sendFailure);
                                if (point == null) {
                                    throw INVALID_POINT.create();
                                }
                                points.add(point);
                            }
                            PathHandler.setWaypoints(points);
                            source.sendSuccess(Translations.EXPORTER_SUCCESSFUL_READ.translate(), true);
                        } catch (IOException e) {
                            source.sendFailure(Translations.EXPORTER_IO_ERROR.translate(e.getMessage()));
                        }
                        return points.size();
                    })
              );
    }

    static ArgumentBuilder<CommandSourceStack, ?> registerSave() {
        return Commands.literal("save")
              .then(Commands.argument("fileName", StringArgumentType.word())
                    .executes(ctx -> {
                        String fileName = StringArgumentType.getString(ctx, "fileName");
                        Position[] points = PathHandler.getWaypoints();
                        if (points.length == 0) {
                            throw SubCommandCam.PATH_IS_EMPTY.create();
                        }
                        File file = MinemaResurrection.getCameraDirectory().resolve(fileName + extension).toFile();
                        if (file.isFile()) {
                            throw FILE_ALREADY_EXISTS.create();
                        }
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)))) {
                            for (Position point : points) {
                                writer.write(point.toString());
                                writer.newLine();
                            }
                            ctx.getSource().sendSuccess(Translations.EXPORTER_SUCCESSFUL_WRITE.translate(file.getAbsolutePath()), true);
                        } catch (IOException e) {
                            ctx.getSource().sendFailure(Translations.EXPORTER_IO_ERROR.translate(e.getMessage()));
                        }
                        return points.length;
                    })
              );
    }
}