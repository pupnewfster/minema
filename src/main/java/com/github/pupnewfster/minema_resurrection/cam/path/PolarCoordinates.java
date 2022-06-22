package com.github.pupnewfster.minema_resurrection.cam.path;

import net.minecraft.world.phys.Vec3;

public record PolarCoordinates(float pitch, float yaw) {

    public static final PolarCoordinates DUMMY = new PolarCoordinates(0, 0);

    public static PolarCoordinates lookAt(Vec3 target, Vec3 from) {
        Vec3 cartesianCoordinates = from.vectorTo(target).normalize();

        double pitch = Math.asin(cartesianCoordinates.y);
        double yaw = Math.atan2(cartesianCoordinates.z, cartesianCoordinates.x);

        // Into degrees
        pitch = Math.toDegrees(pitch);
        yaw = Math.toDegrees(yaw);

        // Minecraft specific corrections
        pitch = -pitch;
        yaw -= 90;
        return new PolarCoordinates((float) pitch, (float) yaw);
    }
}