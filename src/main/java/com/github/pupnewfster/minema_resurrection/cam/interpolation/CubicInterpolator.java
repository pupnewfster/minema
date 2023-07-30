package com.github.pupnewfster.minema_resurrection.cam.interpolation;

import com.github.pupnewfster.minema_resurrection.cam.path.PolarCoordinates;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import net.minecraft.world.phys.Vec3;

public final class CubicInterpolator implements IPositionInterpolator, IPolarCoordinatesInterpolator, IAdditionalAngleInterpolator {

    public static final CubicInterpolator instance = new CubicInterpolator();

    private CubicInterpolator() {
    }

    @Override
    public void interpolatePosition(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setPosition(catmullRomSplinePos(y0, y1, y2, y3, step));
    }

    @Override
    public void interpolatePolarCoordinates(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setPolarCoordinates(new PolarCoordinates(cubic(y0.pitch, y1.pitch, y2.pitch, y3.pitch, (float) step),
              cubic(y0.yaw, y1.yaw, y2.yaw, y3.yaw, (float) step)));
    }

    @Override
    public void interpolateAdditionAngles(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setRoll(cubic(y0.roll, y1.roll, y2.roll, y3.roll, (float) step));
        builder.setFov(cubic(y0.fov, y1.fov, y2.fov, y3.fov, (float) step));
    }

    /**
     * Interpolates between y1 and y2. This is a simple cubic interpolation
     *
     * @param delta from 0 to 1
     */
    private static float cubic(float y0, float y1, float y2, float y3, float delta) {
        float a = y3 - y2 - y0 + y1;
        float b = y0 - y1 - a;
        float c = y2 - y0;
        return ((a * delta + b) * delta + c) * delta + y1;
    }

    //From vanilla's Mth class in version 1.19.2
    private static Vec3 catmullRomSplinePos(Vec3 y0, Vec3 y1, Vec3 y2, Vec3 y3, double step) {
        double d0 = ((-step + 2.0D) * step - 1.0D) * step * 0.5D;
        double d1 = ((3.0D * step - 5.0D) * step * step + 2.0D) * 0.5D;
        double d2 = ((-3.0D * step + 4.0D) * step + 1.0D) * step * 0.5D;
        double d3 = (step - 1.0D) * step * step * 0.5D;
        return new Vec3(
              y0.x * d0 + y1.x * d1 + y2.x * d2 + y3.x * d3,
              y0.y * d0 + y1.y * d1 + y2.y * d2 + y3.y * d3,
              y0.z * d0 + y1.z * d1 + y2.z * d2 + y3.z * d3
        );
    }
}