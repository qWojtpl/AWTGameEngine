package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.scenes.SceneLoader;

public class GameLoop extends Thread {

    private final double FPS = 60;

    @Override
    public void run() {
        while(true) {
            SceneLoader.updateScene();
            Main.getPanel().repaint();
            try {
                Thread.sleep((long) (1000 / FPS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
