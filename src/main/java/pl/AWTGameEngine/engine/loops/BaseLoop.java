package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.windows.Window;

@SuppressWarnings("BusyWait")
public abstract class BaseLoop extends Thread {

    protected final Window window;
    private final String loopName;
    private double targetFps = 1;
    private double actualFps = 0;
    private double actualFpsIterator = 0;
    private boolean killed = false;

    public BaseLoop(Window window, String loopName) {
        this.window = window;
        this.loopName = loopName;
        this.setName(loopName);
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (window.getWindowListener().isOpened() && !killed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    break;
                }
                actualFps = actualFpsIterator;
                actualFpsIterator = 0;
                try {
                    everySecondIteration();
                } catch(Exception e) {
                    Logger.exception("Unhandled exception caught while running an every-second iteration of " + loopName, e);
                    kill();
                    break;
                }
            }
        }, loopName + "-everySecond").start();
        while(window.getWindowListener().isOpened() && !killed) {
            try {
                if(getTargetFps() != 0) {
                    if(getTargetFps() / 2 < getActualFps()) {
                        Thread.sleep((long) (1000 / getTargetFps()));
                    }
                }
            } catch (InterruptedException ignored) {
                break;
            }
            try {
                iteration();
            } catch(Exception e) {
                Logger.exception("Unhandled exception caught while running an iteration of " + loopName, e);
                kill();
                break;
            }
            actualFpsIterator++;
        }
    }

    protected abstract void iteration();

    protected abstract void everySecondIteration();

    public void kill() {
        Logger.warning(loopName + " was killed.");
        this.killed = true;
    }

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
