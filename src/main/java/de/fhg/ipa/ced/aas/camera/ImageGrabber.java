package de.fhg.ipa.ced.aas.camera;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageGrabber implements IImageGrabber{

    FrameGrabber grabber;
    Java2DFrameConverter converter;
    Frame frame;
    private volatile boolean running = true;

    public ImageGrabber(int deviceNumber) {
        grabber = new OpenCVFrameGrabber(deviceNumber);
        converter = new Java2DFrameConverter();
        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> running = false));

        Thread thread = new Thread(() -> {
            while (running) {
                readFrame();
            }
        });
        thread.start();
    }

    void readFrame() {
        try {
            frame = grabber.grab();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBase64Frame() {
        BufferedImage image = converter.getBufferedImage(frame);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }
}
