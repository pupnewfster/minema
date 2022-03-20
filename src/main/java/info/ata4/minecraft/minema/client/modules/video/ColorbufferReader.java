package info.ata4.minecraft.minema.client.modules.video;

import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL12.GL_BGR;

import com.mojang.blaze3d.pipeline.RenderTarget;
import info.ata4.minecraft.minema.Utils;
import org.lwjgl.opengl.GL15;

public class ColorbufferReader extends CommonReader {

    public ColorbufferReader(int width, int height, boolean isPBO) {
        super(width, height, 3, GL_UNSIGNED_BYTE, GL_BGR, isPBO);
    }

    @Override
    public boolean readPixels() {
        // set alignment flags
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        if (isPBO) {
            GL15.glBindBuffer(PBO_TARGET, frontName);
            RenderTarget fb = MC.getMainRenderTarget();
            fb.bindRead();
            glGetTexImage(GL_TEXTURE_2D, 0, FORMAT, TYPE, 0);
            fb.unbindRead();

            // copy back-buffer
            GL15.glBindBuffer(PBO_TARGET, backName);
            buffer = GL15.glMapBuffer(PBO_TARGET, PBO_ACCESS, bufferSize, buffer);
            GL15.glUnmapBuffer(PBO_TARGET);
            GL15.glBindBuffer(PBO_TARGET, 0);

            // If mapping threw an error -> crash immediately please
            Utils.checkGlError();

            // swap PBOs
            int swapName = frontName;
            frontName = backName;
            backName = swapName;
        } else {
            RenderTarget fb = MC.getMainRenderTarget();
            fb.bindRead();
            glGetTexImage(GL_TEXTURE_2D, 0, FORMAT, TYPE, buffer);
            fb.unbindRead();
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
