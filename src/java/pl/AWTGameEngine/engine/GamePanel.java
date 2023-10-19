package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;

import java.awt.*;

public class GamePanel extends NestedPanel {

    private final int WIDTH = 480;
    private final int HEIGHT = (int) (WIDTH * 0.5625);
    private int multipler = 3;

    public GamePanel(Window window) {
        super(window);
        if(window.isFullScreen()) {
            multipler = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / WIDTH);
        }
        this.setPreferredSize(new Dimension(WIDTH * multipler, HEIGHT * multipler));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
    }

    public int getMultipler() {
        return this.multipler;
    }

}