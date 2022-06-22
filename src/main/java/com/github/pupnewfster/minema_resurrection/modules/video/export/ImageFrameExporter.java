/*
 ** 2014 July 30
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.modules.video.export;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ImageFrameExporter extends FrameExporter {

    @Override
    protected void doExportFrame(ByteBuffer buffer) throws Exception {
        String fileName = String.format("%06d.tga", CaptureSession.singleton.getTime().getNumFrames());
        Path folder = CaptureSession.singleton.getCaptureDir().resolve(movieName);
        //Ensure all parents actually exist
        Files.createDirectories(folder);
        Path path = folder.resolve(fileName);
        writeImage(path, buffer, width, height);
    }

    private void writeImage(Path path, ByteBuffer buffer, int width, int height) throws IOException {
        ByteBuffer tgah = ByteBuffer.allocate(18);
        tgah.order(ByteOrder.LITTLE_ENDIAN);

        // image type - uncompressed true-color image
        tgah.position(2);
        tgah.put((byte) 2);

        // width and height
        tgah.position(12);
        tgah.putShort((short) (width & 0xffff));
        tgah.putShort((short) (height & 0xffff));

        // bits per pixel
        tgah.position(16);
        tgah.put((byte) 24);

        tgah.rewind();

        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            fc.write(tgah);
            fc.write(buffer);
            buffer.rewind();
        }
    }
}