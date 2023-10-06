package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.scenes.SceneLoader;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;

public class GameLoop extends Thread {

    private final Window window;
    private final double FPS = 60;

    public GameLoop(Window window) {
        this.window = window;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep((long) (1000 / FPS));
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

}