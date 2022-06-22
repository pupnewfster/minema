package com.github.pupnewfster.minema_resurrection.modules;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import com.github.pupnewfster.minema_resurrection.util.reflection.PrivateAccessor;

public class ShaderSync extends CaptureModule {

    private static ShaderSync instance = null;

    /*
     * Timespan between frames is 1 / framesPerSecond (same as frequency and period in physics) ->
     * the shader mod just measures the time between frames, in this context it is a constant time
     */
    private float frameTimeCounter_step;
    private float fixedFrameTimeCounter;

    @Override
    protected void doEnable() {
        MinemaConfig cfg = MinemaResurrection.instance.getConfig();

        float fps = (float) cfg.frameRate.get();
        float speed = cfg.engineSpeed.get();

        fixedFrameTimeCounter = PrivateAccessor.getFrameTimeCounter();
        frameTimeCounter_step = speed / fps;

        instance = this;
    }

    @Override
    protected boolean checkEnable() {
        return MinemaResurrection.instance.getConfig().syncEngine.get();
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

    public static void setFrameTimeCounter() {
        // This spot is right here because I can choose to only synchronize when
        // recording right here
        if (instance != null) {
            instance.sync();
        }
    }
}