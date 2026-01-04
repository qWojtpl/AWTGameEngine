package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

public class NetLoop extends BaseLoop {

    public NetLoop(Window window) {
        super(window, "NetLoop");
    }

    @Override
    protected void iteration() {
        if(window.getCurrentScene() != null) {
            for(Scene scene : window.getScenes()) {
                scene.netUpdate();
            }
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
