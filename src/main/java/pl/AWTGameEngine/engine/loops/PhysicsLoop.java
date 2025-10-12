package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.windows.Window;

public class PhysicsLoop extends BaseLoop {

    public PhysicsLoop(Window window) {
        super(window, "PhysicsLoop");
    }

    @Override
    public void iteration() {
        if(window.getCurrentScene() != null) {
            for(PanelObject panel : window.getPanels()) {
                panel.updatePhysics();
            }
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
