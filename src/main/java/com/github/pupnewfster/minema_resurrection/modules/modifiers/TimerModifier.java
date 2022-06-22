/*
 ** 2014 July 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.modules.modifiers;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import com.github.pupnewfster.minema_resurrection.engine.FixedTimer;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule;
import net.minecraft.client.Timer;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TimerModifier extends CaptureModule {

    private static FixedTimer timer = null;
    private float defaultTps;

    @Override
    protected void doEnable() {
        Timer defaultTimer = minecraft.timer;
        // check if it's modified already
        if (defaultTimer instanceof FixedTimer) {
            MinemaResurrection.logger.warn("Timer is already modified!");
            return;
        }

        // get default ticks per second if possible
        defaultTps = 1_000 / defaultTimer.msPerTick;

        MinemaConfig cfg = MinemaResurrection.instance.getConfig();
        float fps = (float) cfg.frameRate.get();
        float speed = cfg.engineSpeed.get();

        // set fixed delay timer
        timer = new FixedTimer(defaultTps, fps, speed);
        minecraft.timer = timer;
    }

    @Override
    protected boolean checkEnable() {
        return MinemaResurrection.instance.getConfig().syncEngine.get() & minecraft.hasSingleplayerServer();
    }

    @Override
    protected void doDisable() {
        // check if it's still modified
        if (minecraft.timer instanceof FixedTimer) {
            //restore default timer
            timer = null;
            minecraft.timer = new Timer(defaultTps, 0);
        } else {
            MinemaResurrection.logger.warn("Timer is already restored!");
        }
    }
}