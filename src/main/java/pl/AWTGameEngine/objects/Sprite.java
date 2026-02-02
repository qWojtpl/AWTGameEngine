package pl.AWTGameEngine.objects;

import javafx.scene.image.WritableImage;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Sprite {

    private final String imagePath;
    private BufferedImage image;
    private String base64;

    public Sprite(String imagePath, BufferedImage image) {
        this.imagePath = imagePath;
        this.image = image;
    }

    public Sprite(WritableImage writableImage) {
        this.imagePath = "WritableImage";
        this.image = ImageHelper.imageToBufferedImage(writableImage);
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void updateBufferedImage(BufferedImage bufferedImage) {
        this.image = bufferedImage;
    }

    public Sprite toNegative() {
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int p = image.getRGB(x, y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                r = 255 - r;
                g = 255 - g;
                b = 255 - b;

                p = (a<<24) | (r<<16) | (g<<8) | b;

                image.setRGB(x, y, p);
            }
        }

        base64 = null;

        return this;
    }

    public Sprite scale(double scale) {
        int scaledWidth = (int) (image.getWidth() * scale);
        int scaledHeight = (int) (image.getHeight() * scale);
        BufferedImage sampled = new BufferedImage(scaledWidth, scaledHeight, image.getType());

        Graphics2D sampledGraphics = sampled.createGraphics();
        sampledGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        sampledGraphics.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        sampledGraphics.dispose();

        Graphics2D imageGraphics = image.createGraphics();
        imageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        imageGraphics.drawImage(sampled, 0, 0, image.getWidth(), image.getHeight(), null);
        imageGraphics.dispose();

        base64 = null;

        return this;
    }

    public String getImageBase64() {
        return getImageBase64(false);
    }

    public String getImageBase64(boolean cacheResult) {
        if(base64 != null && cacheResult) {
            return base64;
        }
        String returnable;
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            String[] split = imagePath.split("\\.");
            ImageIO.write(image, split[split.length - 1], stream);
            byte[] imageBytes = stream.toByteArray();
            returnable = new String(Base64.getEncoder().encode(imageBytes), StandardCharsets.UTF_8);
        } catch(IOException e) {
            Logger.exception("Cannot get Base64 from image " + imagePath, e);
            return null;
        }
        if(cacheResult) {
            base64 = returnable;
        }
        return returnable;
    }

    @Override
    public String toString() {
        return this.imagePath;
    }

}
