package pl.AWTGameEngine.engine.panels;

import javafx.embed.swing.JFXPanel;
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
    private final Camera camera;
    private final GraphicsManager3D graphicsManager3D;
    private final javafx.scene.Group rootGroup;
    private final javafx.scene.Scene fxScene;
    private MouseListener mouseListener;

    private final javafx.scene.Group cameraYaw = new Group();
    private final javafx.scene.Group cameraPitch = new Group();

    public Panel3D(Window window, int width, int height) {
        this.window = window;
        this.camera = new Camera(this);
        this.graphicsManager3D = new GraphicsManager3D(this);
        this.rootGroup = new Group();
        this.fxScene = new Scene(rootGroup, width, height, true, SceneAntialiasing.BALANCED);
        initCamera(0.01f, 60000);
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

    public Group getCameraYaw() {
        return this.cameraYaw;
    }

    public Group getCameraPitch() {
        return this.cameraPitch;
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

    @Override
    public void unload() {
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

    public void updateCamera3D() {
        cameraPitch.setTranslateX(camera.getX());
        cameraPitch.setTranslateY(camera.getY());
        cameraPitch.setTranslateZ(camera.getZ());

        cameraPitch.setRotationAxis(Rotate.X_AXIS);
        cameraPitch.setRotate(camera.getRotation().getX());
        cameraYaw.setRotationAxis(Rotate.Y_AXIS);
        cameraYaw.setRotate(camera.getRotation().getY());
        javafx.scene.Camera cam3d = fxScene.getCamera();
        cam3d.setRotationAxis(Rotate.Z_AXIS);
        cam3d.setRotate(camera.getRotation().getZ());
    }

    private void initCamera(float nearClip, float farClip) {
        PerspectiveCamera cam3d = new PerspectiveCamera(true);
        cam3d.setNearClip(nearClip);
        cam3d.setFarClip(farClip);
        cameraPitch.getChildren().add(cam3d);
        cameraYaw.getChildren().add(cameraPitch);
        rootGroup.getChildren().add(cameraYaw);

        cameraPitch.getTransforms().add(new Rotate(0, Rotate.X_AXIS));
        cameraYaw.getTransforms().add(new Rotate(0, Rotate.Y_AXIS));

        fxScene.setCamera(cam3d);
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
