package pl.AWTGameEngine.engine.panels;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.windows.Window;

public class Panel3D extends JFXPanel implements PanelObject {

    private final Window window;
    private final javafx.scene.Scene fxScene;
    private MouseListener mouseListener;
    private final Camera camera;

    public Panel3D(Window window, int width, int height) {
        this.window = window;
        this.camera = new Camera(this);
        this.fxScene = new Scene(new Group(new Box(500, 500, 500) {{
            setMaterial(new PhongMaterial() {{
                setDiffuseMap(new Image("sprites/beaver.jpg"));
            }});
            setTranslateZ(2000);
        }}), width, height, true);
        fxScene.setFill(Color.BLUE);
        fxScene.setCamera(new PerspectiveCamera());
        setMouseListener(new MouseListener(this));
        setScene(fxScene);
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

    @Override
    public void update() {

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


}
