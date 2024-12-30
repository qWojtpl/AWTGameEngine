package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GraphicsManager {

    private Graphics graphics;
    private Graphics2D graphics2D;
    private AffineTransform oldTransform;

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

    public void fillOval(int x, int y, int width, int height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.fillOval(x, y, width, height);
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

    public void drawImage(Sprite image, int x, int y, int width, int height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawImage(image.getImage(), x, y, width, height, null);
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
        if(options == null) {
            options = new RenderOptions();
        }
        graphics.setColor(options.getColor());
        graphics.setFont(options.getFont());
        graphics2D.setStroke(new BasicStroke(options.getStroke()));
        if(oldTransform == null) {
            int rotation = options.getRotation();
            if(rotation != 0) {
                oldTransform = graphics2D.getTransform();
                AffineTransform transform = new AffineTransform();
                transform.rotate(Math.toRadians(rotation), options.getRotationCenterX(), options.getRotationCenterY());
                graphics2D.transform(transform);
            }
        } else {
            graphics2D.setTransform(oldTransform);
            oldTransform = null;
        }
    }

    public void rollBackOptions() {
        readOptions(new RenderOptions());
    }

    public Graphics getGraphics() {
        return this.graphics;
    }

    public void setGraphics(Graphics g) {
        this.graphics = g;
        this.graphics2D = (Graphics2D) g;
    }

    public static class RenderOptions {

        private Color color = Color.BLACK;
        private Font font = Dependencies.getWindowsManager().getDefaultFont();
        private int rotation = 0;
        private int rotationCenterX = 0;
        private int rotationCenterY = 0;
        private float stroke = 1;
        private GameObject context;

        public Color getColor() {
            return this.color;
        }

        public Font getFont() {
            return this.font;
        }

        public int getRotation() {
            return this.rotation;
        }

        public int getRotationCenterX() {
            return this.rotationCenterX;
        }

        public int getRotationCenterY() {
            return this.rotationCenterY;
        }

        public float getStroke() {
            return this.stroke;
        }

        public GameObject getContext() {
            return this.context;
        }

        public RenderOptions setColor(Color color) {
            this.color = color;
            return this;
        }

        public RenderOptions setColor(ColorObject color) {
            return setColor(color.getColor());
        }

        public RenderOptions setFont(Font font) {
            this.font = font;
            return this;
        }

        public RenderOptions setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        public RenderOptions setRotationCenterX(int x) {
            this.rotationCenterX = x;
            return this;
        }

        public RenderOptions setRotationCenterY(int y) {
            this.rotationCenterY = y;
            return this;
        }

        public RenderOptions setStroke(float stroke) {
            this.stroke = stroke;
            return this;
        }

        public RenderOptions setContext(GameObject context) {
            this.context = context;
            return this;
        }

    }

}
