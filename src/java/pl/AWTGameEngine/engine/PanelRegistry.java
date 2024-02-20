package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.engine.panels.NestedPanel;
import pl.AWTGameEngine.engine.panels.PanelObject;

import java.util.ArrayList;
import java.util.List;

public class PanelRegistry {

    private final List<PanelObject> panels = new ArrayList<>();

    public void addPanel(PanelObject panel) {
        panels.add(panel);
    }

    public void removePanel(PanelObject panel) {
        panels.remove(panel);
    }

    public List<PanelObject> getPanels() {
        return new ArrayList<>(panels);
    }

}
