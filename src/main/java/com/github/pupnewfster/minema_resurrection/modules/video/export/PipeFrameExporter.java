/*
 ** 2014 July 29
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.modules.video.export;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.fml.unsafe.UnsafeHacks;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class PipeFrameExporter extends FrameExporter {

    private Process proc;
    private WritableByteChannel pipe;

    @Override
    protected void doExportFrame(ByteBuffer buffer) throws Exception {
        if (pipe.isOpen()) {
            pipe.write(buffer);
            buffer.rewind();
        }
    }

    @Override
    public void enable(String movieName, int width, int height) throws Exception {
        super.enable(movieName, width, height);

        MinemaConfig cfg = MinemaResurrection.instance.getConfig();
        Path path = CaptureSession.singleton.getCaptureDir();

        String params = cfg.videoEncoderParams.get();
        if (width % 2 == 1 || height % 2 == 1) {
            //If it doesn't scale properly specify scaling as part of the parameters
            // Do this first so that we can then replace the width and height as part of that
            params = params.replace("%OPTIONAL_SCALE%", ",scale=trunc(%WIDTH%/2)*2:trunc(%HEIGHT%/2)*2");
        } else {
            params = params.replace("%OPTIONAL_SCALE%", "");
        }
        params = params.replace("%WIDTH%", String.valueOf(width));
        params = params.replace("%HEIGHT%", String.valueOf(height));
        params = params.replace("%FPS%", String.valueOf(cfg.frameRate.get()));
        params = params.replace("%NAME%", movieName);

        List<String> cmds = new ArrayList<>();
        cmds.add(cfg.videoEncoderPath.get());
        cmds.addAll(Arrays.asList(StringUtils.split(params, ' ')));

        // build encoder process and redirect output
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.directory(path.toFile());
        pb.redirectErrorStream(true);
        pb.redirectOutput(path.resolve(movieName + ".log").toFile());
        proc = pb.start();

        // Java wraps the process output stream into a BufferedOutputStream,
        // but its little buffer is just slowing everything down with the huge
        // amount of data we're dealing here, so unwrap it with this little
        // hack.
        OutputStream os = proc.getOutputStream();
        if (os instanceof FilterOutputStream) {
            Field outField = FilterOutputStream.class.getDeclaredField("out");
            os = UnsafeHacks.getField(outField, os);
        }

        pipe = Channels.newChannel(os);
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        try {
            if (pipe != null && pipe.isOpen()) {
                pipe.close();
            }
        } catch (IOException ex) {
            MinemaResurrection.logger.warn("Pipe not closed properly", ex);
        }
        try {
            if (proc != null) {
                proc.waitFor(1, TimeUnit.MINUTES);
                proc.destroy();
            }
        } catch (InterruptedException ex) {
            MinemaResurrection.logger.warn("Pipe program termination interrupted", ex);
        }
    }
}