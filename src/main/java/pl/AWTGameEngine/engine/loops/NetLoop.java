package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;

import java.util.ArrayList;

public class NetLoop extends BaseLoop {

    public NetLoop(BaseWindow window) {
        super(window, "NetLoop");
    }

    @Override
    protected void iteration() {
        for(Scene scene : new ArrayList<>(window.getScenes())) {
            scene.netUpdate();
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
