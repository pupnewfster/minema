package info.ata4.minecraft.minema.client.modules.modifiers;

import com.mojang.blaze3d.pipeline.RenderTarget;
import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import info.ata4.minecraft.minema.client.modules.CaptureModule;
import org.lwjgl.glfw.GLFW;

public class DisplaySizeModifier extends CaptureModule {

    private int originalWidth;
    private int originalHeight;
    private boolean aaFastRenderFix;

    @Override
    protected void doEnable() {
        MinemaConfig cfg = Minema.instance.getConfig();
        originalWidth = MC.getWindow().getScreenWidth();//Display.getWidth();
        originalHeight = MC.getWindow().getScreenHeight();//Display.getHeight();

        aaFastRenderFix = cfg.aaFastRenderFix.get();

        resize(cfg.getFrameWidth(), cfg.getFrameHeight());

        if (aaFastRenderFix) {
            resize(cfg.getFrameWidth(), cfg.getFrameHeight());
            //Display.setDisplayMode(new DisplayMode(cfg.getFrameWidth(), cfg.getFrameHeight()));
            //Display.update();
        } else {
            // render framebuffer texture in original size
            //if (OpenGlHelper.isFramebufferEnabled()) {
            setFramebufferTextureSize(originalWidth, originalHeight);
        }
    }

    @Override
    protected boolean checkEnable() {
        return Minema.instance.getConfig().useFrameSize();
    }

    @Override
    protected void doDisable() {
        if (aaFastRenderFix) {
            resize(originalWidth, originalHeight);
            //Display.setDisplayMode(new DisplayMode(originalWidth, originalHeight));
            // Fix MC-68754
            //Display.setResizable(false);
            //Display.setResizable(true);
        }
        resize(originalWidth, originalHeight);
    }

    public void resize(int width, int height) {
        //MC.resize(width, height);
        GLFW.glfwSetWindowSize(MC.getWindow().getWindow(), width, height);
    }

    public void setFramebufferTextureSize(int width, int height) {
        RenderTarget fb = MC.getMainRenderTarget();
        fb.width = width;
        fb.height = height;
    }
}