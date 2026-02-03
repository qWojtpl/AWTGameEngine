package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
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
        PhysXManager.getInstance().createScene(scene);
        setMouseListener(new MouseListener(window));
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
        if(getWindow().getCurrentScene() == null) {
            return;
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsPreUpdate")) {
            component.onPhysicsPreUpdate();
        }

        PhysXManager.getInstance().simulateFrame(scene, getWindow().getPhysicsLoop().getTargetFps());

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsUpdate")) {
            component.onPhysicsUpdate();
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsAfterUpdate")) {
            component.onPhysicsAfterUpdate();
        }
    }
    @Override
    public void unload() {
        window.remove(this);
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
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

    @Override
    public void printToGraphics(Graphics2D g) {
        super.print(g);
    }

}
