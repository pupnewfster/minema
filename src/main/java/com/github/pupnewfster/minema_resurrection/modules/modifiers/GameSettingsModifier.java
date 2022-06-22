/*
 ** 2014 August 01
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.modules.modifiers;

import com.github.pupnewfster.minema_resurrection.modules.CaptureModule;
import net.minecraft.client.Options;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class GameSettingsModifier extends CaptureModule {

    private int framerateLimit;
    private boolean vSync;
    private boolean pauseOnLostFocus;

    @Override
    protected void doEnable() throws Exception {
        Options gs = minecraft.options;

        // disable build-in framerate limit
        framerateLimit = gs.framerateLimit().get();
        gs.framerateLimit().set(Options.UNLIMITED_FRAMERATE_CUTOFF);

        // disable vSync
        vSync = gs.enableVsync().get();
        gs.enableVsync().set(false);

        // don't pause when losing focus
        pauseOnLostFocus = gs.pauseOnLostFocus;
        gs.pauseOnLostFocus = false;
    }

    @Override
    protected void doDisable() throws Exception {
        // restore everything
        Options gs = minecraft.options;
        gs.framerateLimit().set(framerateLimit);
        gs.pauseOnLostFocus = pauseOnLostFocus;
        gs.enableVsync().set(vSync);
    }

    @Override
    protected boolean checkEnable() {
        return true;
    }
}