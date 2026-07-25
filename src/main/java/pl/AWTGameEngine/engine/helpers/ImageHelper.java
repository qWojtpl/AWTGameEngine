package pl.AWTGameEngine.engine.helpers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ImageHelper {

    public static BufferedImage imageToBufferedImage(Image fxImage) {
        int width = (int) Math.ceil(fxImage.getWidth());
        int height = (int) Math.ceil(fxImage.getHeight());

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);

        int[] buffer = new int[width];

        PixelReader reader = fxImage.getPixelReader();
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbInstance();
        for (int y = 0; y < height; y++) {
            reader.getPixels(0, y, width, 1, format, buffer, 0, width);
            image.getRaster().setDataElements(0, y, width, 1, buffer);
        }

        return image;
    }

    public static BufferedImage bytesToBufferedImage(ByteBuffer buffer, int width, int height) {
        buffer.rewind();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height];
        for(int i = 0; i < pixels.length; i++) {
            int r = buffer.get() & 0xFF;
            int g = buffer.get() & 0xFF;
            int b = buffer.get() & 0xFF;
            int a = buffer.get() & 0xFF;

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

}
