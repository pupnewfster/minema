package com.github.pupnewfster.minema_resurrection.cam.interpolation;

import com.github.pupnewfster.minema_resurrection.cam.path.PolarCoordinates;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class LinearInterpolator implements IPositionInterpolator, IPolarCoordinatesInterpolator, IAdditionalAngleInterpolator {

    public static final LinearInterpolator instance = new LinearInterpolator();

    private LinearInterpolator() {
    }

    @Override
    public void interpolatePosition(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setPosition(new Vec3(Mth.lerp(step, y1.x, y2.x), Mth.lerp(step, y1.y, y2.y), Mth.lerp(step, y1.z, y2.z)));
    }

    @Override
    public void interpolatePolarCoordinates(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setPolarCoordinates(new PolarCoordinates((float) Mth.lerp(step, y1.pitch, y2.pitch), (float) Mth.lerp(step, y1.yaw, y2.yaw)));
    }

    @Override
    public void interpolateAdditionAngles(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setRoll((float) Mth.lerp(step, y1.roll, y2.roll));
        builder.setFov((float) Mth.lerp(step, y1.fov, y2.fov));
    }
}