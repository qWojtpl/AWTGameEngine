package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;

public class GameLoop extends Thread {

    private final Window window;
    private final boolean renderLoop;
    private double FPS = 1;

    public GameLoop(Window window, boolean renderLoop) {
        this.window = window;
        this.renderLoop = renderLoop;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while(window.getWindowListener().isOpened()) {
            try {
                Thread.sleep((long) (1000 / getFPS()));
            } catch(InterruptedException ignored) {
                break;
            }
            if(window.getCurrentScene() != null) {
                if(renderLoop) {
                    window.getPanel().updateRender();
                } else {
                    window.getCurrentScene().update();
                    window.getPanel().updateScene();
                }
            }
        }
    }

    public double getFPS() {
        return FPS;
    }

    public boolean isRenderLoop() {
        return this.renderLoop;
    }

    public void setFPS(double FPS) {
        if(FPS < 1) {
            FPS = 1;
        }
        this.FPS = FPS;
    }

}