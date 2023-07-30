package com.github.pupnewfster.minema_resurrection.modules.video;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import com.github.pupnewfster.minema_resurrection.event.CaptureEvent;
import com.github.pupnewfster.minema_resurrection.event.MinemaEventbus;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule;
import com.github.pupnewfster.minema_resurrection.modules.video.export.FrameExporter;
import com.github.pupnewfster.minema_resurrection.modules.video.export.ImageFrameExporter;
import com.github.pupnewfster.minema_resurrection.modules.video.export.PipeFrameExporter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL;

public class VideoHandler extends CaptureModule {

    private ColorbufferReader colorReader;
    private FrameExporter colorExport;

    private DepthbufferReader depthReader;
    private FrameExporter depthExport;
    private ByteBuffer depthRemapping;

    private int startWidth;
    private int startHeight;
    private boolean recordGui;

    @Override
    protected void doEnable() throws Exception {
        MinemaConfig cfg = MinemaResurrection.instance.getConfig();

        this.startWidth = minecraft.getWindow().getWidth();
        this.startHeight = minecraft.getWindow().getHeight();
        int frameWidth = MinemaResurrection.instance.getConfig().getFrameWidth();
        int frameHeight = MinemaResurrection.instance.getConfig().getFrameHeight();

        String colorName = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.ROOT).format(new Date());
        String depthName = colorName + "depthBuffer";
        this.recordGui = cfg.recordGui.get();

        boolean usePBO = GL.getCapabilities().GL_ARB_pixel_buffer_object;
        boolean usePipe = cfg.useVideoEncoder.get();
        boolean recordDepth = cfg.captureDepth.get();

        colorReader = new ColorbufferReader(frameWidth, frameHeight, usePBO);
        colorExport = usePipe ? new PipeFrameExporter() : new ImageFrameExporter();

        if (recordDepth) {
            depthReader = new DepthbufferReader(frameWidth, frameHeight, usePBO);
            depthExport = usePipe ? new PipeFrameExporter() : new ImageFrameExporter();
            depthRemapping = ByteBuffer.allocateDirect(frameWidth * frameHeight * 3);
            depthRemapping.rewind();
        }

        if (!MinemaResurrection.instance.getConfig().useVideoEncoder.get()) {
            Path captureDir = CaptureSession.singleton.getCaptureDir();
            Path colorDir = captureDir.resolve(colorName);
            if (!Files.exists(colorDir)) {
                Files.createDirectory(colorDir);
            }
            if (recordDepth) {
                Path depthDir = captureDir.resolve(depthName);
                if (!Files.exists(depthDir)) {
                    Files.createDirectory(depthDir);
                }
            }
        }

        colorExport.enable(colorName, frameWidth, frameHeight);
		if (depthExport != null) {
			depthExport.enable(depthName, frameWidth, frameHeight);
		}

        MinemaEventbus.midRenderBUS.registerListener(this::onRenderMid);
        MinemaEventbus.endRenderBUS.registerListener(this::onRenderEnd);
    }

    @Override
    protected void doDisable() throws Exception {
        colorReader.destroy();
        colorExport.destroy();
        colorReader = null;
        colorExport = null;

		if (depthReader == null) {
			return;
		}
        depthReader.destroy();
        depthExport.destroy();
        depthExport = null;
        depthReader = null;
        depthRemapping = null;
    }

    @Override
    protected boolean checkEnable() {
        return true;
    }

    private void onRenderMid(CaptureEvent.Mid e) throws Exception {
        checkDimensions();

        if (depthReader != null) {
            depthExport.waitForLastExport();
            if (depthReader.readPixels()) {
                ByteBuffer floats = depthReader.buffer;

                while (floats.hasRemaining()) {
                    float f = floats.getFloat();
                    byte b = (byte) (linearizeDepth(f) * 255);
                    depthRemapping.put(b);
                    depthRemapping.put(b);
                    depthRemapping.put(b);
                }

                floats.rewind();
                depthRemapping.rewind();

                depthExport.exportFrame(depthRemapping);
            }
        }

        if (!recordGui) {
            exportColor();
            e.session.getTime().nextFrame();
        }
    }

    private void exportColor() throws Exception {
        colorExport.waitForLastExport();
        if (colorReader.readPixels()) {
            colorExport.exportFrame(colorReader.buffer);
        }
    }

    private float linearizeDepth(float z) {
        float near = 0.05f;
        float far = Minecraft.getInstance().options.renderDistance().get() << 4;
        return 0.1f / (far + near - (2 * z - 1) * (far - near));
    }

    private void onRenderEnd(CaptureEvent.End e) throws Exception {
        checkDimensions();
        if (recordGui) {
            exportColor();
            e.session.getTime().nextFrame();
        }
    }

    private void checkDimensions() {
        if (minecraft.getWindow().getWidth() != startWidth || minecraft.getWindow().getHeight() != startHeight) {
            throw new IllegalStateException(String.format("Display size changed! Current: %dx%d Start: %dx%d",
                  minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), startWidth, startHeight));
        }
    }
}