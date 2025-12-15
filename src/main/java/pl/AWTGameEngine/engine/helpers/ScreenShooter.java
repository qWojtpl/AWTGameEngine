package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenShooter {

    public static Sprite takeScreenshot(PanelObject panel) {
        return new Sprite("ScreenShooter", createImage(panel));
    }

    public static BufferedImage createImage(PanelObject panel) {
        int w = panel.getWindow().getWidth();
        int h = panel.getWindow().getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.printToGraphics(g);
        g.dispose();
        return bi;
    }

}
