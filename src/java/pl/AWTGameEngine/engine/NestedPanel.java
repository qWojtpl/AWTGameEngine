package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NestedPanel extends JPanel {

    private final Window window;
    private final Camera camera;
    private GameObject parentObject = null;

    public NestedPanel(GameObject parentObject) {
        super();
        setLayout(null);
        setBackground(Color.WHITE);
        this.window = parentObject.getScene().getWindow();
        this.camera = new Camera(this);
        this.parentObject = parentObject;
    }

    public NestedPanel(Window window) {
        super();
        setLayout(null);
        setBackground(Color.WHITE);
        this.window = window;
        this.camera = new Camera(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(g == null || window.getCurrentScene() == null) {
            return;
        }
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        List<GameObject> renderList = new ArrayList<>();
        for(int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if(this.equals(go.getPanel())) {
                    renderList.add(go);
                    go.preRender(g);
                }
            }
        }
        for(GameObject go : renderList) {
            go.render(g);
        }
        for(GameObject go : renderList) {
            go.afterRender(g);
        }
    }

    public Window getWindow() {
        return this.window;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public GameObject getParentObject() {
        return this.parentObject;
    }

}
