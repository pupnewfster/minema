package info.ata4.minecraft.minema.client.modules.video;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.lwjgl.opengl.GL;
import info.ata4.minecraft.minema.CaptureSession;
import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import info.ata4.minecraft.minema.client.event.EndRenderEvent;
import info.ata4.minecraft.minema.client.event.MidRenderEvent;
import info.ata4.minecraft.minema.client.event.MinemaEventbus;
import info.ata4.minecraft.minema.client.modules.CaptureModule;
import info.ata4.minecraft.minema.client.modules.video.export.FrameExporter;
import info.ata4.minecraft.minema.client.modules.video.export.ImageFrameExporter;
import info.ata4.minecraft.minema.client.modules.video.export.PipeFrameExporter;
import net.minecraft.client.Minecraft;

public class VideoHandler extends CaptureModule {

	private ColorbufferReader colorReader;
	private FrameExporter colorExport;

	private DepthbufferReader depthReader;
	private FrameExporter depthExport;
	private ByteBuffer depthRemapping;

	private String colorName;
	private String depthName;
	private int startWidth;
	private int startHeight;
	private boolean recordGui;

	@Override
	protected void doEnable() throws Exception {
		MinemaConfig cfg = Minema.instance.getConfig();

		this.startWidth = MC.getMainWindow().getFramebufferWidth();
		this.startHeight = MC.getMainWindow().getFramebufferHeight();
		this.colorName = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
		this.depthName = colorName.concat("depthBuffer");
		this.recordGui = cfg.recordGui.get();

		boolean usePBO = GL.getCapabilities().GL_ARB_pixel_buffer_object;
		boolean usePipe = cfg.useVideoEncoder.get();
		boolean recordDepth = cfg.captureDepth.get();

		colorReader = new ColorbufferReader(startWidth, startHeight, usePBO);
		colorExport = usePipe ? new PipeFrameExporter() : new ImageFrameExporter();

		if (recordDepth) {
			depthReader = new DepthbufferReader(startWidth, startHeight, usePBO);
			depthExport = usePipe ? new PipeFrameExporter() : new ImageFrameExporter();
			depthRemapping = ByteBuffer.allocateDirect(startWidth * startHeight * 3);
			depthRemapping.rewind();
		}

		if (!Minema.instance.getConfig().useVideoEncoder.get()) {
			Path colorDir = CaptureSession.singleton.getCaptureDir().resolve(colorName);
			Path depthDir = CaptureSession.singleton.getCaptureDir().resolve(depthName);

			if (!Files.exists(colorDir)) {
				Files.createDirectory(colorDir);
			}
			if (recordDepth && !Files.exists(depthDir)) {
				Files.createDirectory(depthDir);
			}
		}

		colorExport.enable(colorName, startWidth, startHeight);
		if (depthExport != null)
			depthExport.enable(depthName, startWidth, startHeight);

		MinemaEventbus.midRenderBUS.registerListener((e) -> onRenderMid(e));
		MinemaEventbus.endRenderBUS.registerListener((e) -> onRenderEnd(e));
	}

	@Override
	protected void doDisable() throws Exception {
		colorReader.destroy();
		colorExport.destroy();
		colorReader = null;
		colorExport = null;

		if (depthReader == null)
			return;
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

	private void onRenderMid(MidRenderEvent e) throws Exception {
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
		final float near = 0.05f;
		final float far = Minecraft.getInstance().gameSettings.renderDistanceChunks << 4;
		return 0.1f / (far + near - (2 * z - 1) * (far - near));
	}

	private void onRenderEnd(EndRenderEvent e) throws Exception {
		checkDimensions();

		if (recordGui) {
			exportColor();

			e.session.getTime().nextFrame();
		}

	}

	private void checkDimensions() {
		if (MC.getMainWindow().getFramebufferWidth() != startWidth || MC.getMainWindow().getFramebufferHeight() != startHeight) {
			throw new IllegalStateException(String.format("Display size changed! Current: %dx%d Start: %dx%d",
					MC.getMainWindow().getFramebufferWidth(), MC.getMainWindow().getFramebufferHeight(), startWidth, startHeight));
		}
	}

}
