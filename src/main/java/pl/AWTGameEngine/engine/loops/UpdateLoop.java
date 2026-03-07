package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.util.ArrayList;

public class UpdateLoop extends BaseLoop {

    public UpdateLoop(Window window) {
        super(window, "UpdateLoop");
    }

    @Override
    public void iteration() {
        for(Scene scene : new ArrayList<>(window.getScenes())) {
            scene.update();
        }
        if(window.getMouseListener() != null) {
            window.getMouseListener().refresh();
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
