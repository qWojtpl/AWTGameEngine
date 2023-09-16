package pl.AWTGameEngine;

public class GameLoop extends Thread {

    private double FPS = 60;

    /*@Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while(true) {
            Main.getInstance().paint(Main.getInstance().getGraphics());
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;
                if(remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

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
                FPSCounter.call();
                Main.getInstance().paint(Main.getInstance().getGraphics());
                repaint();
                delta--;
            }
        }
    }

    public void repaint() {
        Main.getInstance().repaint();
    }

}
