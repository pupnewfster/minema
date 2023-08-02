package com.github.pupnewfster.minema_resurrection.cam.interpolation;

import com.github.pupnewfster.minema_resurrection.cam.path.Position;

public final class Interpolator {

    private final IPositionInterpolator positionInterpolator;
    private final IPolarCoordinatesInterpolator polarCoordinatesInterpolator;
    private final IAdditionalAngleInterpolator additionalAngleInterpolator;
    private final ITimeInterpolator timeInterpolator;

    private final Position[] points;

    /**
     * is the actual "length" of the array which is points.length - 1 (visually the length of the path you have to go from index 0 to n)
     */
    private final int pathLength;

    public Interpolator(Position[] points, IPositionInterpolator positionInterpolator, IPolarCoordinatesInterpolator polarCoordinatesInterpolator,
          IAdditionalAngleInterpolator additionalAngleInterpolator, ITimeInterpolator timeInterpolator) {
        this.points = points;
        this.pathLength = this.points.length - 1;

        this.positionInterpolator = positionInterpolator;
        this.polarCoordinatesInterpolator = polarCoordinatesInterpolator;
        this.additionalAngleInterpolator = additionalAngleInterpolator;
        this.timeInterpolator = timeInterpolator;
    }

    public Position getPoint(long currentIteration, long iterations) {
        double pathPosition = currentIteration / (double) iterations;
        double section = pathPosition * this.pathLength;

        int section1 = (int) section;
        if (section1 == this.pathLength) {
            // pathPosition unavoidably reaches the last node at the very end ->
            // correcting that
            section1--;
        }
        int section2 = section1 + 1;

        double step = section - section1;

        int section0 = section1 - 1;
        int section3 = section2 + 1;

        // Bounding the outer nodes inside the array if necessary
        // I could extrapolate these points, but this is a quick and acceptable
        // solution: It even creates some kind of fade in and out
        if (section0 < 0) {
            section0 = 0;
        }
        if (section3 > this.pathLength) {
            section3 = this.pathLength;
        }

        Position y0 = this.points[section0];
        Position y1 = this.points[section1];
        Position y2 = this.points[section2];
        Position y3 = this.points[section3];

        PositionBuilder builder = new PositionBuilder();

        this.positionInterpolator.interpolatePosition(builder, y0, y1, y2, y3, step);
        this.polarCoordinatesInterpolator.interpolatePolarCoordinates(builder, y0, y1, y2, y3, step);
        this.additionalAngleInterpolator.interpolateAdditionAngles(builder, y0, y1, y2, y3, step);
        this.timeInterpolator.interpolateTime(builder, y0, y1, y2, y3, step);

        return builder.build();
    }
}