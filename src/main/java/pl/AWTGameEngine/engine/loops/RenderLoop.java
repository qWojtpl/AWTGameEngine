package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.windows.Window;

public class RenderLoop extends BaseLoop {

    public RenderLoop(Window window) {
        super(window, "RenderLoop", false);
    }

    @Override
    public void iteration() {
        if(window.getCurrentScene() != null) {
            for(PanelObject panel : window.getPanels()) {
                panel.updateRender();
            }
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
