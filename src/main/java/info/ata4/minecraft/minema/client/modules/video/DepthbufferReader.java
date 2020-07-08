package info.ata4.minecraft.minema.client.modules.video;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glReadPixels;
import org.lwjgl.opengl.GL15;

public class DepthbufferReader extends CommonReader {

	public DepthbufferReader(int width, int height, boolean isPBO, boolean isFBO) {
		super(width, height, 4, GL_FLOAT, GL_DEPTH_COMPONENT, isPBO, isFBO);
	}

	@Override
	public boolean readPixels() {
		// set alignment flags
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		// Cannot read Minecraft's framebuffer even if it is active, as the depth buffer
		// is not a texture
		if (isPBO) {
			GL15.glBindBuffer(PBO_TARGET, frontName);

			glReadPixels(0, 0, width, height, FORMAT, TYPE, 0);

			// copy back-buffer
			GL15.glBindBuffer(PBO_TARGET, backName);
			buffer = GL15.glMapBuffer(PBO_TARGET, PBO_ACCESS, bufferSize, buffer);
			GL15.glUnmapBuffer(PBO_TARGET);
			GL15.glBindBuffer(PBO_TARGET, 0);

			// If mapping threw an error -> crash immediately please
			int err = GL15.glGetError();
			if (err != 0) {
			    System.err.println("OpenGL returned non-zero error code: " + err);
			    Thread.dumpStack();
			    System.exit(0);
			}

			// swap PBOs
			int swapName = frontName;
			frontName = backName;
			backName = swapName;
		} else {
			glReadPixels(0, 0, width, height, FORMAT, TYPE, buffer);
		}

		buffer.rewind();

		// first frame is empty in PBO mode, don't export it
		if (isPBO & firstFrame) {
			firstFrame = false;
			return false;
		}

		return true;
	}

}
