package pl.AWTGameEngine.engine;

import java.awt.*;

public class GraphicsManager {

    private Graphics graphics;

    public void drawLine(int x1, int y1, int x2, int y2) {
        if(graphics == null) {
            return;
        }
        graphics.drawLine(x1, y1, x2, y2);
    }

    public void drawOval(int x, int y, int width, int height) {
        if(graphics == null) {
            return;
        }
        graphics.drawOval(x, y, width, height);
    }

    public void drawRect(int x, int y, int width, int height) {
        if(graphics == null) {
            return;
        }
        graphics.drawRect(x, y, width, height);
    }

    public void fillRect(int x, int y, int width, int height) {
        if(graphics == null) {
            return;
        }
        graphics.fillRect(x, y, width, height);
    }

    public void drawImage(Image image, int x, int y, int width, int height) {
        graphics.drawImage(image, x, y, width, height, null);
    }

    public void drawString(String string, int x, int y) {
        graphics.drawString(string, x, y);
    }

    public Graphics getGraphics() {
        return this.graphics;
    }

    public void setGraphics(Graphics g) {
        this.graphics = g;
    }

    public void setColor(Color color) {
        if(graphics == null) {
            return;
        }
        graphics.setColor(color);
    }

}
