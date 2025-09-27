package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.windows.Window;

public class UpdateLoop extends BaseLoop {

    public UpdateLoop(Window window) {
        super(window);
    }

    @Override
    public void iteration() {
        if(window.getCurrentScene() != null) {
            window.getCurrentScene().update();
        }
    }

    @Override
    protected void everySecondIteration() {
        if(window.getCurrentScene() != null) {
            window.getCurrentScene().updateSecond();
        }
    }

}
