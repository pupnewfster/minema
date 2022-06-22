package com.github.pupnewfster.minema_resurrection.util;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum Translations {
    COMMAND_PATH_STARTED("command", "path.started"),
    COMMAND_PATH_STOPPED("command", "path.stopped"),
    COMMAND_PATH_IS_EMPTY("command", "path.is_empty"),
    COMMAND_PATH_INVALID_POINT("command", "path.invalid_point"),
    COMMAND_PATH_IS_POPULATED("command", "path.is_populated"),
    COMMAND_PATH_UNDO("command", "path.undo"),
    COMMAND_PATH_DOES_NOT_EXIST("command", "path.does_not_exist"),
    COMMAND_PATH_RESET("command", "path.reset"),
    COMMAND_PATH_RESET_BEWARE_TARGET("command", "path.reset_beware_target"),
    COMMAND_PATH_REPLACE("command", "path.replace"),
    COMMAND_PATH_ADD("command", "path.add"),
    COMMAND_PATH_SET_TARGET("command", "path.target.set"),
    COMMAND_PATH_REMOVED_TARGET("command", "path.target.removed"),
    COMMAND_PATH_CIRCLE_CREATED("command", "path.circle_created"),
    COMMAND_PATH_POINT_INSERTED("command", "path.point.inserted"),
    COMMAND_PATH_POINT_REMOVED("command", "path.point.removed"),

    COMMAND_TRAVELLED_TO("command", "travelled_to"),
    COMMAND_AT_LEAST_TWO_POINTS("command", "at_least_two_points"),
    //Exporter
    EXPORTER_SUCCESSFUL_WRITE("exporter", "successful.write"),
    EXPORTER_SUCCESSFUL_READ("exporter", "successful.read"),
    EXPORTER_IO_ERROR("exporter", "io_error"),
    EXPORTER_POSITION_CANNOT_BE_PARSED("exporter", "position_cannot_be_parsed"),
    EXPORTER_FILE_DOES_NOT_EXIST("exporter", "file_does_not_exist"),
    EXPORTER_FILE_ALREADY_EXISTS("exporter", "file_already_exists"),
    //Render
    RENDER_PREVIEW_ON("render", "preview.on"),
    RENDER_PREVIEW_OFF("render", "preview.off"),
    //Warnings
    WARN_NO_LOCAL_WORLD_TELEPORT("warn", "no_local_world_teleport"),
    WARN_ERROR("warn", "error"),
    WARN_WARNING("warn", "warning"),
    WARN_TICK_SYNC("warn", "tick_sync"),
    WARN_PRELOAD_CHUNKS("warn", "preload_chunks"),
    //Errors
    ERROR_CAUSE("error", "cause"),
    ERROR_SEE_LOG("error", "see_log"),
    ;

    private final String key;

    Translations(String type, String path) {
        this(Util.makeDescriptionId(type, MinemaResurrection.rl(path)));
    }

    Translations(String key) {
        this.key = key;
    }

    public MutableComponent translate(Object... args) {
        return Component.translatable(key, args);
    }

    public MutableComponent translateColored(ChatFormatting color, Object... args) {
        return translate(args).withStyle(color);
    }
}