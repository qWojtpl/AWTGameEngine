package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.objects.RenderOptions;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GraphicsManager {

    private Graphics graphics;
    private Graphics2D graphics2D;
    private AffineTransform oldTransform;

    public void drawLine(double x1, double y1, double x2, double y2, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        rollBackOptions();
    }

    public void drawOval(double x, double y, double width, double height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawOval((int) x, (int) y, (int) width, (int) height);
        rollBackOptions();
    }

    public void fillOval(double x, double y, double width, double height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.fillOval((int) x, (int) y, (int) width, (int) height);
        rollBackOptions();
    }

    public void drawRect(double x, double y, double width, double height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawRect((int) x, (int) y, (int) width, (int) height);
        rollBackOptions();
    }

    public void fillRect(double x, double y, double width, double height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.fillRect((int) x, (int) y, (int) width, (int) height);
        rollBackOptions();
    }

    public void drawImage(Sprite image, double x, double y, double width, double height, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawImage(image.getImage(), (int) Math.ceil(x), (int) Math.ceil(y), (int) Math.ceil(width), (int) Math.ceil(height), null);
        rollBackOptions();
    }

    public void drawString(String string, double x, double y, RenderOptions renderOptions) {
        if(graphics == null) {
            return;
        }
        readOptions(renderOptions);
        graphics.drawString(string, (int) x, (int) y);
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
            double rotation = options.getRotation();
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

}
