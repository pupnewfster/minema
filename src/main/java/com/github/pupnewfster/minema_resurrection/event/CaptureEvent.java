package com.github.pupnewfster.minema_resurrection.event;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public abstract class CaptureEvent {

    public final CaptureSession session;

    public CaptureEvent(CaptureSession session) {
        this.session = session;
    }

    /**
     * Is posted when the render pipeline is right before clearing the depth buffer for rendering hand, GUI and other stuff
     * <p>
     * See {@link RenderLevelStageEvent} and {@link RenderLevelStageEvent.Stage#AFTER_LEVEL} for details
     */
    public static class Mid extends CaptureEvent {

        public Mid(CaptureSession session) {
            super(session);
        }
    }

    public static class End extends CaptureEvent {

        public End(CaptureSession session) {
            super(session);
        }
    }
}