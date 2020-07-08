/*
 ** 2014 August 01
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.minecraft.minema.client.modules.modifiers;

import info.ata4.minecraft.minema.client.modules.CaptureModule;
import net.minecraft.client.GameSettings;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class GameSettingsModifier extends CaptureModule {

	private int framerateLimit;
	private boolean vSync;
	private boolean pauseOnLostFocus;

	@Override
	protected void doEnable() throws Exception {
		GameSettings gs = MC.gameSettings;

		// disable build-in framerate limit
		framerateLimit = gs.framerateLimit;
		gs.framerateLimit = Integer.MAX_VALUE;

		// disable vSync
		vSync = gs.vsync;
		gs.vsync = false;

		// don't pause when losing focus
		pauseOnLostFocus = gs.pauseOnLostFocus;
		gs.pauseOnLostFocus = false;
	}

	@Override
	protected void doDisable() throws Exception {
		// restore everything
		GameSettings gs = MC.gameSettings;
		gs.framerateLimit = framerateLimit;
		gs.pauseOnLostFocus = pauseOnLostFocus;
		gs.vsync = vSync;
	}

	@Override
	protected boolean checkEnable() {
		return true;
	}

}
