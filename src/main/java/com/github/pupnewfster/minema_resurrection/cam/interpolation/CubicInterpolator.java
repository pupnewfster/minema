package com.github.pupnewfster.minema_resurrection.cam.interpolation;

import com.github.pupnewfster.minema_resurrection.cam.path.PolarCoordinates;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import net.minecraft.util.Mth;

public final class CubicInterpolator implements IPositionInterpolator, IPolarCoordinatesInterpolator, IAdditionalAngleInterpolator {

    public static final CubicInterpolator instance = new CubicInterpolator();

    private CubicInterpolator() {
    }

    @Override
    public void interpolatePosition(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        builder.setPosition(Mth.catmullRomSplinePos(y0, y1, y2, y3, step));
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
}