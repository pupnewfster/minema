/*
 ** 2012 March 31
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.modules;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule.EventBasedCaptureModule;
import com.github.pupnewfster.minema_resurrection.util.CaptureTime;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Minema information screen overlay.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class CaptureOverlay extends EventBasedCaptureModule {

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text evt) {
        CaptureTime time = CaptureSession.singleton.getTime();

        List<String> left = evt.getLeft();
        if (minecraft.options.renderDebug) {
            // F3 menu is open -> add spacer
            left.add("");
        }
        left.add("Frame: " + time.getNumFrames());

        left.add("Rate: " + Minecraft.fps + " fps");
        left.add("Avg.: " + (int) time.getAverageFPS() + " fps");
        left.add("Delay: " + CaptureTime.getTimeUnit(time.getPreviousCaptureTime()));

        left.add("Time R: " + time.getRealTimeString());
        left.add("Time V: " + time.getVideoTimeString());
    }

    @Override
    protected boolean checkEnable() {
        return MinemaResurrection.instance.getConfig().showOverlay.get();
    }
}