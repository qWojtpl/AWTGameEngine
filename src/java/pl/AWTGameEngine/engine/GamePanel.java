package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;

import java.awt.*;

public class GamePanel extends NestedPanel {

    private final int WIDTH = 480;
    private final int HEIGHT = (int) (WIDTH * 0.5625);
    private double multiplier = 3;

    public GamePanel(Window window) {
        super(window);
        if(window.isFullScreen()) {
            multiplier = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / WIDTH);
        }
        this.setPreferredSize(new Dimension((int) (WIDTH * multiplier), (int) (HEIGHT * multiplier)));
        this.setDoubleBuffered(true);
    }

    public GamePanel(Window window, double multiplier) {
        super(window);
        this.multiplier = multiplier;
        if(window.isFullScreen()) {
            multiplier = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / WIDTH);
        }
        this.setPreferredSize(new Dimension((int) (WIDTH * multiplier), (int) (HEIGHT * multiplier)));
        this.setDoubleBuffered(true);
    }

    public double getMultiplier() {
        return this.multiplier;
    }

}