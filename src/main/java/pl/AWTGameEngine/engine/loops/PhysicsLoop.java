package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.util.ArrayList;

public class PhysicsLoop extends BaseLoop {

    public PhysicsLoop(Window window) {
        super(window, "PhysicsLoop");
    }

    @Override
    public void iteration() {
        for(Scene scene : new ArrayList<>(window.getScenes())) {
            scene.physicsUpdate();
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
