package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.engine.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Sprite {

    private final String imagePath;
    private final Image image;
    private String base64;

    public Sprite(String imagePath, Image image) {
        this.imagePath = imagePath;
        this.image = image;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public Image getImage() {
        return this.image;
    }

    public String getImageBase64() {
        return getImageBase64(false);
    }

    public String getImageBase64(boolean cacheResult) {
        if(base64 != null) {
            return base64;
        }
        String returnable;
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            String[] split = imagePath.split("\\.");
            ImageIO.write((BufferedImage) image, split[split.length - 1], stream);
            byte[] imageBytes = stream.toByteArray();
            returnable = new String(Base64.getEncoder().encode(imageBytes), StandardCharsets.UTF_8);
        } catch(IOException e) {
            Logger.log("Cannot get Base64 from image " + imagePath, e);
            return null;
        }
        if(cacheResult) {
            base64 = returnable;
        }
        return returnable;
    }

}
