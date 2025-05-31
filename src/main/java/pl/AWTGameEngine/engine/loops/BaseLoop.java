package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.windows.Window;

public abstract class BaseLoop extends Thread {

    protected final Window window;
    protected double FPS = 1;

    public BaseLoop(Window window) {
        this.window = window;
    }

    @Override
    public abstract void run();

    public Window getWindow() {
        return this.window;
    }

    public double getFPS() {
        return this.FPS;
    }

    public void setFPS(double FPS) {
        if(FPS < 0) {
            FPS = 0;
        }
        this.FPS = FPS;
    }

}
