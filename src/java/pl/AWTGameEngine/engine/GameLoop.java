package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameLoop extends Thread {

    private final Window window;
    private double FPS = 1;

    public GameLoop(Window window) {
        this.window = window;
    }

    @Override
    public void run() {
        while(window.getWindowListener().isOpened()) {
            try {
                Thread.sleep((long) (1000 / getFPS()));
            } catch(InterruptedException ignored) {
                break;
            }
            window.getCurrentScene().update();
            for(NestedPanel panel : window.getCurrentScene().getPanelRegistry().getPanels()) {
                panel.repaint();
            }
        }
    }

    public double getFPS() {
        return FPS;
    }

    public void setFPS(double FPS) {
        if(FPS < 1) {
            FPS = 1;
        }
        this.FPS = FPS;
    }

}