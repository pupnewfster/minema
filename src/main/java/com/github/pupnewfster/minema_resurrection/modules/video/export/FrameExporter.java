package com.github.pupnewfster.minema_resurrection.modules.video.export;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class FrameExporter {

    protected final ExecutorService exportService;
    protected String movieName;
    protected int width;
    protected int height;
    protected Future<?> exportFuture;

    public FrameExporter() {
        exportService = Executors.newSingleThreadExecutor();
    }

    public void enable(String movieName, int width, int height) throws Exception {
        this.movieName = movieName;
        this.width = width;
        this.height = height;
    }

    public void destroy() throws Exception {
        exportService.shutdown();

        try {
            if (!exportService.awaitTermination(3, TimeUnit.SECONDS)) {
                MinemaResurrection.logger.warn("Frame export service termination timeout");
                exportService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            MinemaResurrection.logger.warn("Frame export service termination interrupted", ex);
        }
    }

    public final void waitForLastExport() throws Exception {
        // wait for the previous task to complete before sending the next one
        try {
            if (exportFuture != null) {
                exportFuture.get();
            }
        } catch (InterruptedException ex) {
            // catch uncritical interruption exception
            MinemaResurrection.logger.warn("Frame export task interrupted", ex);
        }
    }

    public final void exportFrame(ByteBuffer buffer) throws Exception {
        // export frame in the background so that the next frame can be
        // rendered in the meantime
        exportFuture = exportService.submit(() -> {
            try {
                doExportFrame(buffer);
            } catch (Exception ex) {
                throw new RuntimeException("Can't export frame " + CaptureSession.singleton.getTime().getNumFrames(), ex);
            }
        });
    }

    protected abstract void doExportFrame(ByteBuffer buffer) throws Exception;
}