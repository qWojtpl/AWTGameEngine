package pl.AWTGameEngine;

public class FPSCounter extends Thread {

    public static int calls = 0;

    public static void call() {
        calls++;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("FPS: " + calls);
            calls = 0;
        }
    }

}
