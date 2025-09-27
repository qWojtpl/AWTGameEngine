package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.windows.Window;

public class RenderLoop extends BaseLoop {

    public RenderLoop(Window window) {
        super(window, "RenderLoop");
    }

    @Override
    public void iteration() {
        if(window.getCurrentScene() != null) {
            window.getPanel().updateRender();
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
