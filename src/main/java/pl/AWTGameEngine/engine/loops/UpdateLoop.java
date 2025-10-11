package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

public class UpdateLoop extends BaseLoop {

    public UpdateLoop(Window window) {
        super(window, "UpdateLoop");
    }

    @Override
    public void iteration() {
        if(window.getCurrentScene() != null) {
            for(Scene scene : window.getScenes()) {
                scene.update();
            }
        }
    }

    @Override
    protected void everySecondIteration() {
        if(window.getCurrentScene() != null) {
            for(Scene scene : window.getScenes()) {
                scene.updateSecond();
            }
        }
    }

}
