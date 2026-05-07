package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.windows.BaseWindow;

import java.util.ArrayList;

public class RenderLoop extends BaseLoop {

    public RenderLoop(BaseWindow window) {
        super(window, "RenderLoop");
    }

    @Override
    public void iteration() {
        for(PanelObject panel : new ArrayList<>(window.getPanels())) {
            panel.updateRender();
        }
    }

    @Override
    protected void everySecondIteration() {

    }

}
