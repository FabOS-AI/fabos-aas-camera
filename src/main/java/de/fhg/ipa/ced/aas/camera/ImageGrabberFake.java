package de.fhg.ipa.ced.aas.camera;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageGrabberFake implements IImageGrabber{

    BufferedImage image;

    public ImageGrabberFake() {
        try {
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("image.jpg");
            image = ImageIO.read(resourceStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getBase64Frame() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }
}
