package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;

public class NestedPanel extends JPanel {

    private final Window window;

    public NestedPanel(GameObject parentObject) {
        super();
        setLayout(null);
        this.window = parentObject.getScene().getWindow();
    }

    public NestedPanel(Window window) {
        super();
        setLayout(null);
        this.window = window;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(g == null || window.getCurrentScene() == null) {
            return;
        }
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        for(int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if(this.equals(go.getPanel())) {
                    go.render(g);
                }
            }
        }
    }

}
