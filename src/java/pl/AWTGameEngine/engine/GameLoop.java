package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Main;

public class GameLoop extends Thread {

    private final double FPS = 60;

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while(true) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1) {
                Main.getPanel().repaint();
                delta--;
            }
        }
    }

}
