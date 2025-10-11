package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;

public class DefaultPanel extends JPanel implements PanelObject {

    private final Window window;
    private final Scene scene;
    private final Camera camera;
    private final GraphicsManager graphicsManager = new GraphicsManager();
    private MouseListener mouseListener;

    public DefaultPanel(Scene scene) {
        super();
        setLayout(null);
        setBackground(Color.WHITE);
        this.window = scene.getWindow();
        this.scene = scene;
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
        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPreRender#GraphicsManager")) {
            component.onPreRender(graphicsManager);
        }
        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onRender#GraphicsManager")) {
            component.onRender(graphicsManager);
        }
        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onAfterRender#GraphicsManager")) {
            component.onAfterRender(graphicsManager);
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
        window.remove(this);
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
