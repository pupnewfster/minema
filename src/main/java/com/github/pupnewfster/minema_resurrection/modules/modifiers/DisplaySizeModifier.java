package com.github.pupnewfster.minema_resurrection.modules.modifiers;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class DisplaySizeModifier extends CaptureModule {

    private int originalWidth;
    private int originalHeight;

    private int originalFrameWidth;
    private int originalFrameHeight;

    @Override
    protected void doEnable() {
        MinemaConfig cfg = MinemaResurrection.instance.getConfig();
        originalWidth = minecraft.getWindow().getScreenWidth();
        originalHeight = minecraft.getWindow().getScreenHeight();

        if (cfg.aaFastRenderFix.get()) {
            resize(cfg.getFrameWidth(), cfg.getFrameHeight());
        } else {//render framebuffer texture in original size
            originalFrameWidth = minecraft.getWindow().getWidth();
            originalFrameHeight = minecraft.getWindow().getHeight();
            setFramebufferTextureSize(cfg.getFrameWidth(), cfg.getFrameHeight());
        }
    }

    @Override
    protected boolean checkEnable() {
        return MinemaResurrection.instance.getConfig().useFrameSize();
    }

    @Override
    protected void doDisable() {
        resize(originalWidth, originalHeight);
        if (!MinemaResurrection.instance.getConfig().aaFastRenderFix.get()) {
            setFramebufferTextureSize(originalFrameWidth, originalFrameHeight);
        }
    }

    private void resize(int width, int height) {
        GLFW.glfwSetWindowSize(minecraft.getWindow().getWindow(), width, height);
    }

    private void setFramebufferTextureSize(int width, int height) {
        minecraft.getWindow().setWidth(width);
        minecraft.getWindow().setHeight(height);
        RenderTarget fb = minecraft.getMainRenderTarget();
        fb.resize(width, height, Minecraft.ON_OSX);
        if (minecraft.gameRenderer != null) {
            minecraft.gameRenderer.resize(width, height);
        }
    }
}