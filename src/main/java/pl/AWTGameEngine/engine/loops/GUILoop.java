package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;

import java.util.ArrayList;

public class GUILoop extends BaseLoop {

    public GUILoop(BaseWindow window) {
        super(window, "GUILoop");
    }

    @Override
    protected void iteration() {
        for(Scene scene : new ArrayList<>(window.getScenes())) {
            scene.guiUpdate();
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
