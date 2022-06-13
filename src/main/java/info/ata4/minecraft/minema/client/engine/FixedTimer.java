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

    private final float msPerTick;
    private final float framesPerSecond;
    private final float timerSpeed;

    public FixedTimer(float tps, float fps, float speed) {
        super(tps, 0);
        msPerTick = 1000.0F / tps;
        framesPerSecond = fps;
        timerSpeed = speed;
    }

    @Override
    public int advanceTime(long gameTime) {
        // TODO: What does lastSyncSysClock actually do and do I have to care? Was introduced in 1.13.2
        tickDelta += timerSpeed * (msPerTick / framesPerSecond);
        int elapsedTicks = (int) tickDelta;
        tickDelta -= elapsedTicks;
        partialTick = tickDelta;
        return elapsedTicks;
    }
}