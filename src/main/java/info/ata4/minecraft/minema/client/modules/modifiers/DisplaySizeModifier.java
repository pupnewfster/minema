package info.ata4.minecraft.minema.client.modules.modifiers;

import org.lwjgl.glfw.GLFW;
import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import info.ata4.minecraft.minema.client.modules.CaptureModule;
import net.minecraft.client.shader.Framebuffer;

public class DisplaySizeModifier extends CaptureModule {

	private int originalWidth;
	private int originalHeight;
	private boolean aaFastRenderFix;

	@Override
	protected void doEnable() {
		MinemaConfig cfg = Minema.instance.getConfig();
		originalWidth = MC.getMainWindow().getWidth();//Display.getWidth();
		originalHeight = MC.getMainWindow().getHeight();//Display.getHeight();

		aaFastRenderFix = cfg.aaFastRenderFix.get();

		resize(cfg.getFrameWidth(), cfg.getFrameHeight());

		if (aaFastRenderFix) {
		    resize(cfg.getFrameWidth(), cfg.getFrameHeight());
			//Display.setDisplayMode(new DisplayMode(cfg.getFrameWidth(), cfg.getFrameHeight()));
			//Display.update();
		} else {
			// render framebuffer texture in original size
			//if (OpenGlHelper.isFramebufferEnabled()) {
		    if (MC.getFramebuffer() != null) {
				setFramebufferTextureSize(originalWidth, originalHeight);
			}
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
		GLFW.glfwSetWindowSize(MC.getMainWindow().getHandle(), width, height);
	}

	public void setFramebufferTextureSize(int width, int height) {
		Framebuffer fb = MC.getFramebuffer();
		fb.framebufferTextureWidth = width;
		fb.framebufferTextureHeight = height;
	}

}
