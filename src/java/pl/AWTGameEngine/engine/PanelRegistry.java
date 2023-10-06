package pl.AWTGameEngine.engine;

import java.util.ArrayList;
import java.util.List;

public class PanelRegistry {

    private final List<NestedPanel> panels = new ArrayList<>();

    public void addPanel(NestedPanel panel) {
        panels.add(panel);
    }

    public void removePanel(NestedPanel panel) {
        panels.remove(panel);
    }

    public List<NestedPanel> getPanels() {
        return new ArrayList<>(panels);
    }

}
