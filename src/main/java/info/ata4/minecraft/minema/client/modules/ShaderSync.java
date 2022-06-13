package info.ata4.minecraft.minema.client.modules;

import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import info.ata4.minecraft.minema.util.reflection.PrivateAccessor;

public class ShaderSync extends CaptureModule {

    private static ShaderSync instance = null;

    /*
     * Timespan between frames is 1/framesPerSecond (same as frequency and period in
     * physics) -> the shader mod just measures the time between frames, in this
     * context it is a constant time
     */
    private float frameTimeCounter_step;
    private float fixedFrameTimeCounter;

    @Override
    protected void doEnable() {
        MinemaConfig cfg = Minema.instance.getConfig();

        float fps = (float) cfg.frameRate.get();
        float speed = cfg.engineSpeed.get();

        fixedFrameTimeCounter = PrivateAccessor.getFrameTimeCounter();
        frameTimeCounter_step = speed / fps;

        instance = this;
    }

    @Override
    protected boolean checkEnable() {
        return Minema.instance.getConfig().syncEngine.get();
    }

    @Override
    protected void doDisable() {
        instance = null;
    }

    private void sync() {
        fixedFrameTimeCounter += frameTimeCounter_step;
        fixedFrameTimeCounter %= 3600.0F;
        PrivateAccessor.setFrameTimeCounter(fixedFrameTimeCounter);
    }

    /**
     * CALLED BY ASM INJECTED CODE! (COREMOD) DO NOT MODIFY METHOD SIGNATURE!
     */
    public static void setFrameTimeCounter() {
        // This spot is right here because I can choose to only synchronize when
        // recording right here
        if (instance == null) {
            return;
        }
        instance.sync();
    }

}
