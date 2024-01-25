package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.ColorObject;

import java.awt.*;

public class GraphicsManager {

    private Graphics graphics;

    public void drawLine(int x1, int y1, int x2, int y2, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawLine(x1, y1, x2, y2);
        rollBackOptions();
    }

    public void drawOval(int x, int y, int width, int height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawOval(x, y, width, height);
        rollBackOptions();
    }

    public void drawRect(int x, int y, int width, int height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawRect(x, y, width, height);
        rollBackOptions();
    }

    public void fillRect(int x, int y, int width, int height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.fillRect(x, y, width, height);
        rollBackOptions();
    }

    public void drawImage(Image image, int x, int y, int width, int height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawImage(image, x, y, width, height, null);
        rollBackOptions();
    }

    public void drawString(String string, int x, int y, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawString(string, x, y);
        rollBackOptions();
    }

    public void readOptions(RenderOptions options) {
        graphics.setColor(RenderOptionsReader.getColor(options));
    }

    public void rollBackOptions() {
        readOptions(new RenderOptions());
    }

    public Graphics getGraphics() {
        return this.graphics;
    }

    public void setGraphics(Graphics g) {
        this.graphics = g;
    }

    public static class RenderOptions {

        private Color color = Color.BLACK;

        public Color getColor() {
            return this.color;
        }

        public RenderOptions setColor(Color color) {
            this.color = color;
            return this;
        }

        public RenderOptions setColor(ColorObject color) {
            return setColor(color);
        }

    }

    private static class RenderOptionsReader {

        public static Color getColor(RenderOptions renderOptions) {
            if(renderOptions == null) {
                return Color.BLACK;
            }
            return renderOptions.getColor();
        }

    }

}
