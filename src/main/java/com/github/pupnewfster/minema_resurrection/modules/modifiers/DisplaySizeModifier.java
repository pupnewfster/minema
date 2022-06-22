package com.github.pupnewfster.minema_resurrection.modules.modifiers;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule;
import org.lwjgl.glfw.GLFW;

public class DisplaySizeModifier extends CaptureModule {

    private int originalWidth;
    private int originalHeight;

    @Override
    protected void doEnable() {
        MinemaConfig cfg = MinemaResurrection.instance.getConfig();
        originalWidth = minecraft.getWindow().getScreenWidth();
        originalHeight = minecraft.getWindow().getScreenHeight();

        resize(cfg.getFrameWidth(), cfg.getFrameHeight());

        if (cfg.aaFastRenderFix.get()) {
            resize(cfg.getFrameWidth(), cfg.getFrameHeight());
        } else {//render framebuffer texture in original size
            setFramebufferTextureSize(originalWidth, originalHeight);
        }
    }

    @Override
    protected boolean checkEnable() {
        return MinemaResurrection.instance.getConfig().useFrameSize();
    }

    @Override
    protected void doDisable() {
        resize(originalWidth, originalHeight);
    }

    private void resize(int width, int height) {
        GLFW.glfwSetWindowSize(minecraft.getWindow().getWindow(), width, height);
    }

    private void setFramebufferTextureSize(int width, int height) {
        RenderTarget fb = minecraft.getMainRenderTarget();
        fb.width = width;
        fb.height = height;
    }
}