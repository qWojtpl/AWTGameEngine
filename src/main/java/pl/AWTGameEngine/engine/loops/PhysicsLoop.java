package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.windows.Window;

public class PhysicsLoop extends BaseLoop {

    public PhysicsLoop(Window window) {
        super(window);
    }

    @Override
    public void iteration() {
        if(window.getCurrentScene() != null) {
            window.getPanel().updatePhysics();
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
