package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;

import java.awt.*;

public class HeadlessPanel implements PanelObject {

    private final Scene scene;
    private final Camera camera;

    public HeadlessPanel(Scene scene) {
        this.scene = scene;
        this.camera = new Camera(this);
        PhysXManager.getInstance().createScene(scene);
    }

    @Override
    public BaseWindow getWindow() {
        return this.scene.getWindow();
    }

    @Override
    public Camera getCamera() {
        return this.camera;
    }

    @Override
    public Scene getParentScene() {
        return null;
    }

    @Override
    public Component add(Component comp) {
        return null;
    }

    @Override
    public void unload() {

    }

    @Override
    public void updateRender() {

    }

    @Override
    public void setSize(Dimension dimension) {

    }

    @Override
    public void setPreferredSize(Dimension dimension) {

    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {

    }

    @Override
    public void setOpaque(boolean opaque) {

    }

    @Override
    public void printToGraphics(Graphics2D g) {

    }

    @Override
    public void onSceneLoad() {

    }

}
