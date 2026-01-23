package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.windows.Window;

@SuppressWarnings("BusyWait")
public abstract class BaseLoop extends Thread {

    protected final Window window;
    private final String loopName;
    private double targetFps = 1;
    private double actualFps = 0;
    private double actualFpsIterator = 0;

    public BaseLoop(Window window, String loopName) {
        this.window = window;
        this.loopName = loopName;
        this.setName(loopName);
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (window.getWindowListener().isOpened()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    break;
                }
                actualFps = actualFpsIterator;
                actualFpsIterator = 0;
                everySecondIteration();
            }
        }, loopName + "-everySecond").start();
        while(window.getWindowListener().isOpened()) {
            try {
                if(getTargetFps() != 0) {
                    if(getTargetFps() / 2 < getActualFps()) {
                        Thread.sleep((long) (1000 / getTargetFps()));
                    }
                }
            } catch (InterruptedException ignored) {
                break;
            }
            iteration();
            actualFpsIterator++;
        }
    }

    protected abstract void iteration();

    protected abstract void everySecondIteration();

    public Window getWindow() {
        return this.window;
    }

    @Command("targetFps")
    public double getTargetFps() {
        return this.targetFps;
    }

    public void setTargetFps(double fps) {
        this.targetFps = fps;
        actualFps = fps;
    }

    @Command("actualFps")
    public double getActualFps() {
        return this.actualFps;
    }

}
