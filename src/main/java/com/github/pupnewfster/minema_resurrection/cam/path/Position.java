package com.github.pupnewfster.minema_resurrection.cam.path;

import com.github.pupnewfster.minema_resurrection.util.Translations;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class Position extends Vec3 {

    private static final String padding = "/";

    public final float pitch;
    public final float yaw;
    public final float roll;
    public final float fov;
    public final long time;

    public Position(double x, double y, double z, float pitch, float yaw, float roll, float fov, long time) {
        super(x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.fov = fov;
        this.time = time;
    }

    @NotNull
    @Override
    public String toString() {
        return this.x + padding + this.y + padding + this.z + padding + this.pitch + padding + this.yaw + padding + this.roll + padding + this.fov + padding + this.time;
    }

    public static Position fromString(String input, Consumer<Component> errorPrinter) {
        String[] parts = input.split(padding);
        if (parts.length >= 7) {
            try {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                float pitch = Float.parseFloat(parts[3]);
                float yaw = Float.parseFloat(parts[4]);
                float roll = Float.parseFloat(parts[5]);
                float fov = Float.parseFloat(parts[6]);
                long time = -1;
                if (parts.length == 8) {
                    time = Long.parseLong(parts[7]);
                }
                return new Position(x, y, z, pitch, yaw, roll, fov, time);
            } catch (NumberFormatException ignored) {
            }
        }
        errorPrinter.accept(Translations.EXPORTER_POSITION_CANNOT_BE_PARSED.translate(input));
        return null;
    }
}