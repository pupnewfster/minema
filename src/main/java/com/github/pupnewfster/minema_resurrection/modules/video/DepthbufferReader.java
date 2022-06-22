package com.github.pupnewfster.minema_resurrection.modules.video;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class DepthbufferReader extends CommonReader {

    public DepthbufferReader(int width, int height, boolean isPBO) {
        super(width, height, 4, GL11.GL_FLOAT, GL11.GL_DEPTH_COMPONENT, isPBO);
    }

    @Override
    public boolean readPixels() {
        // set alignment flags
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Cannot read Minecraft's framebuffer even if it is active, as the depth buffer
        // is not a texture
        if (isPBO) {
            GL15.glBindBuffer(PBO_TARGET, frontName);
            GL11.glReadPixels(0, 0, width, height, FORMAT, TYPE, 0);

            // copy back-buffer
            GL15.glBindBuffer(PBO_TARGET, backName);
            buffer = GL15.glMapBuffer(PBO_TARGET, PBO_ACCESS, bufferSize, buffer);
            GL15.glUnmapBuffer(PBO_TARGET);
            GL15.glBindBuffer(PBO_TARGET, 0);

            // If mapping threw an error -> crash immediately please
            checkGlError();

            // swap PBOs
            int swapName = frontName;
            frontName = backName;
            backName = swapName;
        } else {
            GL11.glReadPixels(0, 0, width, height, FORMAT, TYPE, buffer);
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