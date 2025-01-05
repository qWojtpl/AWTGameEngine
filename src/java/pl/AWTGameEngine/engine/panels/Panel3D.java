package pl.AWTGameEngine.engine.panels;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.util.LinkedHashMap;
import java.util.List;

public class Panel3D extends JFXPanel implements PanelObject {

    private final Window window;
    private final javafx.scene.Scene fxScene;
    private final javafx.scene.Group rootGroup;
    private MouseListener mouseListener;
    private final Camera camera;
    private final GraphicsManager3D graphicsManager3D;

    public Panel3D(Window window, int width, int height) {
        this.window = window;
        this.camera = new Camera(this);
        this.graphicsManager3D = new GraphicsManager3D(this);
        PerspectiveCamera cam3d = new PerspectiveCamera(true);
        this.rootGroup = new Group();
        this.fxScene = new Scene(rootGroup, width, height, true, SceneAntialiasing.BALANCED);
        fxScene.setFill(Color.LIGHTGRAY);
        cam3d.setNearClip(0.01);
        cam3d.setFarClip(6000);
        cam3d.getTransforms().add(new Rotate(0, Rotate.X_AXIS));
        cam3d.getTransforms().add(new Rotate(0, Rotate.Y_AXIS));
        cam3d.getTransforms().add(new Rotate(0, Rotate.Z_AXIS));
        fxScene.setCamera(cam3d);
        setMouseListener(new MouseListener(this));
        setScene(fxScene);
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

    @Override
    public void update() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager3D == null) {
            return;
        }
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        for (int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if(!go.isActive()) {
                    continue;
                }
                if(this.equals(go.getPanel())) {
                    go.render3D(graphicsManager3D);
                }
            }
        }
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

    public void updateCamera3D() {
        javafx.scene.Camera cam3d = fxScene.getCamera();
        Rotate xRot = (Rotate) cam3d.getTransforms().get(0);
        xRot.setAngle(camera.getRotation().getX());
        Rotate yRot = (Rotate) cam3d.getTransforms().get(1);
        yRot.setAngle(camera.getRotation().getY());
        Rotate zRot = (Rotate) cam3d.getTransforms().get(2);
        zRot.setAngle(camera.getRotation().getZ());
        cam3d.setTranslateX(camera.getX());
        cam3d.setTranslateY(camera.getY());
        cam3d.setTranslateZ(camera.getZ());
    }

}
