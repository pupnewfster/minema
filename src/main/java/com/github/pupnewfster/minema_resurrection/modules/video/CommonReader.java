/*
 ** 2016 June 03
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.modules.video;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBPixelBufferObject;
import org.lwjgl.opengl.GL15;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class CommonReader {

    protected static final Minecraft minecraft = Minecraft.getInstance();
    protected static final int PBO_TARGET = ARBPixelBufferObject.GL_PIXEL_PACK_BUFFER_ARB;
    protected static final int PBO_USAGE = GL15.GL_STREAM_READ;
    protected static final int PBO_ACCESS = GL15.GL_READ_ONLY;

    protected final int TYPE;
    protected final int FORMAT;
    protected final boolean isPBO;

    public final int width;
    public final int height;

    /**
     * Might become a native buffer upon the first frame, if PBO is used
     */
    public ByteBuffer buffer;
    protected final int bufferSize;

    protected int frontName;
    protected int backName;
    protected boolean firstFrame;

    public CommonReader(int width, int height, int BPP, int TYPE, int FORMAT, boolean isPBO) {
        this.TYPE = TYPE;
        this.FORMAT = FORMAT;
        this.isPBO = isPBO;
        this.width = width;
        this.height = height;

        bufferSize = width * height * BPP;

        if (isPBO) {
            frontName = GL15.glGenBuffers();
            GL15.glBindBuffer(PBO_TARGET, frontName);
            GL15.glBufferData(PBO_TARGET, bufferSize, PBO_USAGE);

            backName = GL15.glGenBuffers();
            GL15.glBindBuffer(PBO_TARGET, backName);
            GL15.glBufferData(PBO_TARGET, bufferSize, PBO_USAGE);

            GL15.glBindBuffer(PBO_TARGET, 0);

            firstFrame = true;
        } else {
            this.buffer = ByteBuffer.allocateDirect(bufferSize);
            buffer.rewind();
        }
    }

    public abstract boolean readPixels();

    public void destroy() {
        if (isPBO) {
            GL15.glDeleteBuffers(frontName);
            GL15.glDeleteBuffers(backName);
        }
    }

    protected static void checkGlError() {
        int err = GlStateManager._getError();
        if (err != 0) {
            MinemaResurrection.logger.error(LogUtils.FATAL_MARKER, "OpenGL returned non-zero error code: {}", err);
            Thread.dumpStack();
            System.exit(0);
        }
    }
}