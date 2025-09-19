package pl.AWTGameEngine.engine.panels;

import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFX;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

public class PanelFX extends JFXPanel implements PanelObject {

    private final Window window;
    private final Camera camera;
    private final GraphicsManager3D graphicsManager3D;
    private final PhysXManager physXManager;
    private final javafx.scene.Group rootGroup;
    private final javafx.scene.Scene fxScene;
    private MouseListener mouseListener;

    public PanelFX(Window window, int width, int height) {
        this.window = window;
        this.camera = new Camera(this);
        this.graphicsManager3D = new GraphicsManagerFX(this);
        this.physXManager = new PhysXManager();
        this.rootGroup = new Group(new AmbientLight());
        this.fxScene = new Scene(rootGroup, width, height, true, SceneAntialiasing.BALANCED);
        physXManager.init();
        initListeners();
        setScene(fxScene);
        fxScene.setFill(Color.LIGHTGRAY);
    }

    @Override
    public Window getWindow() {
        return this.window;
    }

    @Override
    public Camera getCamera() {
        return this.camera;
    }

    @Override
    public MouseListener getMouseListener() {
        return this.mouseListener;
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
        for(GameObject go : window.getCurrentScene().getGameObjects()) {
            go.render3D(graphicsManager3D);
        }
    }

    @Override
    public void updatePhysics() {
        physXManager.simulateFrame(getWindow().getPhysicsLoop().getFPS());
        for(GameObject go : window.getCurrentScene().getGameObjects()) {
            go.updatePhysics();
        }
    }

    @Override
    public void unload() {
        physXManager.cleanup();
        setScene(null);
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
        setMouseListener(new MouseListener(this));
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

}
