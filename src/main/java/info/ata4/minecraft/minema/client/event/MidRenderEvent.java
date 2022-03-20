package info.ata4.minecraft.minema.client.event;

import info.ata4.minecraft.minema.CaptureSession;
import net.minecraftforge.client.event.RenderLevelLastEvent;

/**
 * Is posted when the render pipeline is right before clearing the depth buffer for rendering hand, GUI and other stuff
 * <p>
 * See {@link RenderLevelLastEvent} for details
 */
public class MidRenderEvent extends CaptureEvent {

    public MidRenderEvent(CaptureSession session) {
        super(session);
    }
}