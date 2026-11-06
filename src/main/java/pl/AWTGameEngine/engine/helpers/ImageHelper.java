package pl.AWTGameEngine.engine.helpers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public static ByteBuffer bufferedImageToByteBuffer(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4);
        buffer.order(ByteOrder.nativeOrder());

        for(int pixel : pixels) {
            buffer.put((byte) ((pixel >> 24) & 0xFF));
            buffer.put((byte) ((pixel >> 16) & 0xFF));
            buffer.put((byte) ((pixel >> 8) & 0xFF));
            buffer.put((byte) (pixel & 0xFF));
        }

        buffer.flip();
        return buffer;
    }

    public static byte[] bufferedImageToByteArray(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        byte[] byteArray = new byte[pixels.length * 4];

        int index = 0;
        for(int pixel : pixels) {
            byte a = (byte) ((pixel >> 24) & 0xFF);
            byte r = (byte) ((pixel >> 16) & 0xFF);
            byte g = (byte) ((pixel >> 8) & 0xFF);
            byte b = (byte) (pixel & 0xFF);
            byteArray[index++] = r;
            byteArray[index++] = g;
            byteArray[index++] = b;
            byteArray[index++] = a;
        }

        return byteArray;
    }

    public static void swapRGBandBGRandBlend(byte[] source, int[] blend) {
        int len = blend.length;
        for (int j = 0; j < len; j++) {
            int i = j * 3;
            int blendPixel = blend[j];
            int alpha = (blendPixel >>> 24) & 0xFF;
            if(alpha == 255) {
                source[i]     = (byte) (blendPixel & 0xFF);
                source[i + 1] = (byte) ((blendPixel >> 8) & 0xFF);
                source[i + 2] = (byte) ((blendPixel >> 16) & 0xFF);
            } else if(alpha == 0) {
                byte t = source[i];
                source[i]     = source[i + 2];
                source[i + 2] = t;
            } else {
                int srcR = source[i] & 0xFF;
                int srcG = source[i + 1] & 0xFF;
                int srcB = source[i + 2] & 0xFF;

                int blnR = blendPixel & 0xFF;
                int blnG = (blendPixel >> 8) & 0xFF;
                int blnB = (blendPixel >> 16) & 0xFF;

                int invAlpha = 255 - alpha;
                source[i]     = (byte) ((blnR * alpha + srcR * invAlpha) >> 8);
                source[i + 1] = (byte) ((blnG * alpha + srcG * invAlpha) >> 8);
                source[i + 2] = (byte) ((blnB * alpha + srcB * invAlpha) >> 8);
            }
        }
    }

}
