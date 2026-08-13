package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.windows.BaseWindow;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseLoop extends Thread {

    protected final BaseWindow window;
    private final String loopName;
    private double targetFps = 1;
    private double actualFps = 0;
    private double actualFpsIterator = 0;
    private volatile boolean killed = false;
    private Runnable killOperation;
    private final ConcurrentLinkedQueue<Runnable> nextFrameOperations = new ConcurrentLinkedQueue<>();

    public BaseLoop(BaseWindow window, String loopName) {
        this.window = window;
        this.loopName = loopName;
        this.setName(loopName);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        while(!killed) {
            long now = System.nanoTime();
            if(targetFps > 0) {
                delta += (now - lastTime) / (1_000_000_000.0 / targetFps);
                lastTime = now;
                if(delta >= 1) {
                    while(delta >= 1) {
                        executeFrame();
                        delta--;
                    }
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
            } else {
                lastTime = now;
                executeFrame();
            }
            if(System.currentTimeMillis() - timer >= 1000) {
                timer += 1000;
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
        }
        if(killOperation != null) {
            killOperation.run();
            killOperation = null;
        }
    }

    private void executeFrame() {
        if(!nextFrameOperations.isEmpty()) {
            Runnable operation;
            while((operation = nextFrameOperations.poll()) != null) {
                try {
                    operation.run();
                } catch(Exception e) {
                    Logger.exception("Unhandled exception caught while running a next frame operation of " + loopName, e);
                    kill();
                }
            }
        }
        try {
            iteration();
        } catch(Exception e) {
            Logger.exception("Unhandled exception caught while running an iteration of " + loopName, e);
            kill();
        }
        actualFpsIterator++;
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
    }

    @Command("actualFps")
    public double getActualFps() {
        return this.actualFps;
    }

}
