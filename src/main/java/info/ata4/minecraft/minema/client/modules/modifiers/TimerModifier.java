/*
 ** 2014 July 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.minecraft.minema.client.modules.modifiers;

import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import info.ata4.minecraft.minema.client.engine.FixedTimer;
import info.ata4.minecraft.minema.client.modules.CaptureModule;
import net.minecraft.util.Timer;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TimerModifier extends CaptureModule {

	private static FixedTimer timer = null;
	private float defaultTps;

	@Override
	protected void doEnable() {
		MinemaConfig cfg = Minema.instance.getConfig();

		Timer defaultTimer = MC.timer;

		// check if it's modified already
		if (defaultTimer instanceof FixedTimer) {
			L.warn("Timer is already modified!");
			return;
		}

		// get default ticks per second if possible
		if (defaultTimer != null) {
			defaultTps = MC.timer.tickLength;
		}

		float fps = (float) cfg.frameRate.get();
		float speed = (float) cfg.engineSpeed.get();

		// set fixed delay timer
		timer = new FixedTimer(defaultTps, fps, speed);
		MC.timer = timer;
	}

	@Override
	protected boolean checkEnable() {
		return Minema.instance.getConfig().syncEngine.get() & MC.isSingleplayer();
	}

	@Override
	protected void doDisable() {
		// check if it's still modified
		if (!(MC.timer instanceof FixedTimer)) {
			L.warn("Timer is already restored!");
			return;
		}

		// restore default timer
		timer = null;
		MC.timer = new Timer(1000F / defaultTps, 0);
	}

}
