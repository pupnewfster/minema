package com.github.pupnewfster.minema_resurrection.cam;

public class CameraRoll {

    private CameraRoll() {
    }

    private static final float anglePerKeyPress = 0.5F;
    public static float roll = 0;

    public static void rotateClockWise() {
        roll += anglePerKeyPress;
    }

    public static void rotateCounterClockWise() {
        roll -= anglePerKeyPress;
    }

    public static void reset() {
        roll = 0;
    }
}