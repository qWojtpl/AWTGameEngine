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

public class DefaultPanel extends JPanel implements PanelObject {

    private final Window window;
    private final Camera camera;
    private final GraphicsManager graphicsManager = new GraphicsManager();
    private MouseListener mouseListener;

    public DefaultPanel(Window window) {
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
        for(GameObject go : window.getCurrentScene().getGameObjects()) {
            go.preRender(graphicsManager);
        }
        for(GameObject go : window.getCurrentScene().getGameObjects()) {
            go.render(graphicsManager);
        }
        for(GameObject go : window.getCurrentScene().getGameObjects()) {
            go.afterRender(graphicsManager);
        }
    }

    @Override
    public void updateRender() {
        repaint();
    }

    @Override
    public void updatePhysics() {

    }

    @Override
    public void unload() {

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
