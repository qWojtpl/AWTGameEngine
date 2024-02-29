package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NestedPanel extends JPanel implements PanelObject {

    private final Window window;
    private final Camera camera;
    private final GraphicsManager graphicsManager = new GraphicsManager();
    private MouseListener mouseListener;
    private GameObject parentObject = null;

    public NestedPanel(GameObject parentObject) {
        super();
        setLayout(null);
        setBackground(Color.WHITE);
        this.window = parentObject.getScene().getWindow();
        this.camera = new Camera(this);
        setMouseListener(new MouseListener(this));
        this.parentObject = parentObject;
    }

    public NestedPanel(Window window) {
        super();
        setLayout(null);
        setBackground(Color.WHITE);
        this.window = window;
        this.camera = new Camera(this);
        setMouseListener(new MouseListener(this));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(g == null || window.getCurrentScene() == null) {
            return;
        }
        graphicsManager.setGraphics(g);
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        List<GameObject> renderList = new ArrayList<>();
        for(int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if(!go.isActive()) {
                    continue;
                }
                if(this.equals(go.getPanel())) {
                    renderList.add(go);
                    go.preRender(graphicsManager);
                }
            }
        }
        for(GameObject go : renderList) {
            go.render(graphicsManager);
        }
        for(GameObject go : renderList) {
            go.afterRender(graphicsManager);
        }
    }

    public void update() {
        repaint();
    }

    public Window getWindow() {
        return this.window;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public GraphicsManager getGraphicsManager() {
        return this.graphicsManager;
    }

    public MouseListener getMouseListener() {
        return this.mouseListener;
    }

    public GameObject getParentObject() {
        return this.parentObject;
    }

    public void setMouseListener(MouseListener mouseListener) {
        if(this.mouseListener != null) {
            removeMouseListener(this.mouseListener);
            removeMouseMotionListener(this.mouseListener);
            removeMouseWheelListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

}
