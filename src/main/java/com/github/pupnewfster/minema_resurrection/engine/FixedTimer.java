package com.github.pupnewfster.minema_resurrection.engine;

import net.minecraft.client.Timer;

/**
 * Extension of Minecraft's default timer for fixed framerate rendering.
 */
public class FixedTimer extends Timer {

    private final float increment;

    public FixedTimer(float tps, float fps, float speed) {
        super(tps, 0);
        increment = speed * (msPerTick / fps);
    }

    @Override
    public int advanceTime(long curTimeMs) {
        tickDelta = increment;
        partialTick += tickDelta;
        int elapsedTicks = (int) partialTick;
        partialTick -= elapsedTicks;
        return elapsedTicks;
    }
}