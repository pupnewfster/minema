/*
 ** 2012 January 3
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.minecraft.minema.client.engine;

import net.minecraft.client.Timer;

/**
 * Extension of Minecraft's default timer for fixed framerate rendering.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de> / Shader part: daipenger
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