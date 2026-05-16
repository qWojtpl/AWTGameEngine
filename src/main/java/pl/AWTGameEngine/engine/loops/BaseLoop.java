package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.windows.BaseWindow;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("BusyWait")
public abstract class BaseLoop extends Thread {

    protected final BaseWindow window;
    private final String loopName;
    private double targetFps = 1;
    private double actualFps = 0;
    private double actualFpsIterator = 0;
    private boolean killed = false;
    private Runnable killOperation;
    private final List<Runnable> nextFrameOperations = new ArrayList<>();

    public BaseLoop(BaseWindow window, String loopName) {
        this.window = window;
        this.loopName = loopName;
        this.setName(loopName);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        new Thread(() -> {
            while(!killed) {
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
            if(!nextFrameOperations.isEmpty()) {
                try {
                    List<Runnable> runnableList = new ArrayList<>(nextFrameOperations);
                    nextFrameOperations.clear();
                    for (Runnable runnable : runnableList) {
                        runnable.run();
                    }
                } catch (Exception e) {
                    Logger.exception("Unhandled exception caught while running a next frame operation of " + loopName, e);
                    kill();
                    break;
                }
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
        if(killOperation != null) {
            killOperation.run();
            killOperation = null;
        }
    }

    protected abstract void iteration();

    protected abstract void everySecondIteration();

    public void kill() {
        if(this.killed) {
            return;
        }
        Logger.warning(loopName + " was killed.");
        this.killed = true;
    }

    public void kill(Runnable operation) {
        killOperation = operation;
        kill();
    }

    public void addNextFrameOperation(Runnable operation) {
        nextFrameOperations.add(operation);
    }

    public BaseWindow getWindow() {
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
