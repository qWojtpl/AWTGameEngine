package pl.AWTGameEngine.engine.panels;

import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFX;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;

public class PanelFX extends JFXPanel implements PanelObject {

    private final Scene scene;
    private final Window window;
    private final Camera camera;
    private final GraphicsManager3D graphicsManager3D;
    private final PhysXManager physXManager;
    private final javafx.scene.Group rootGroup;
    private final javafx.scene.Scene fxScene;
    private MouseListener mouseListener;

    public PanelFX(Scene scene, int width, int height) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.graphicsManager3D = new GraphicsManagerFX(this);
        this.physXManager = PhysXManager.getInstance();
        this.rootGroup = new Group(new AmbientLight());
        this.fxScene = new javafx.scene.Scene(rootGroup, width, height, true, SceneAntialiasing.BALANCED);
        physXManager.init();
        initListeners();
        setScene(fxScene);
        fxScene.setFill(Color.LIGHTGRAY);
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
    }

    @Override
    public Window getWindow() {
        return this.scene.getWindow();
    }

    @Override
    public Camera getCamera() {
        return this.camera;
    }

    public javafx.scene.Scene getFxScene() {
        return this.fxScene;
    }

    public javafx.scene.Group getRootGroup() {
        return this.rootGroup;
    }

    public GraphicsManager3D getGraphicsManager3D() {
        return this.graphicsManager3D;
    }

    public PhysXManager getPhysXManager() {
        return this.physXManager;
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager3D == null) {
            return;
        }
        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("on3DRenderRequest#GraphicsManager3D")) {
            component.on3DRenderRequest(graphicsManager3D);
        }
    }

    @Override
    public void updatePhysics() {
        physXManager.simulateFrame(getWindow().getPhysicsLoop().getTargetFps());
        for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onPhysicsUpdate")) {
            component.onPhysicsUpdate();
        }
    }

    @Override
    public void unload() {
        physXManager.cleanup();
        setScene(null);
        window.remove(this);
    }

    public void setMouseListener(MouseListener mouseListener) {
        if (this.mouseListener != null) {
            removeMouseListener(this.mouseListener);
            removeMouseMotionListener(this.mouseListener);
            removeMouseWheelListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    private void initListeners() {
        setMouseListener(new MouseListener(window));
        fxScene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            getWindow().getKeyListener().asKeyPress(event.getCode().getCode());
        });
        fxScene.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
            getWindow().getKeyListener().asKeyType(event.getCode().getCode());
        });
        fxScene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
            getWindow().getKeyListener().asKeyRelease(event.getCode().getCode());
        });
    }

    @Override
    public void printToGraphics(Graphics2D g) {
        super.print(g);
    }

}
