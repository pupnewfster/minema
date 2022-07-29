package com.github.pupnewfster.minema_resurrection.cam;

import net.minecraft.client.Minecraft;

public class DynamicFOV {

    private DynamicFOV() {
    }

    /**
     * Extends default of max fov
     */
    private static final float upperBound = 150;
    /**
     * Extends default of min fov
     */
    private static final float lowerBound = 5;

    private static final float fovPerKeyPress = 0.25F;
    private static int lastSyncedFOV;
    private static float fov;

    public static void increase() {
        set(get() + fovPerKeyPress);
    }

    public static void decrease() {
        set(get() - fovPerKeyPress);
    }

    public static void reset() {
        fov = lastSyncedFOV = getRaw();
    }

    public static void set(float fov) {
        DynamicFOV.fov = Math.min(Math.max(fov, lowerBound), upperBound);
    }

    public static float get() {
        if (lastSyncedFOV != getRaw()) {
            //Reset fov to the raw value as it was changed by the slider, and we should update to the new value
            reset();
        }
        return fov;
    }

    public static int getRaw() {
        return Minecraft.getInstance().options.fov().get();
    }
}