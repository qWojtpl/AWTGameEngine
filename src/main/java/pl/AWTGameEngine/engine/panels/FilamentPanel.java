package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.OpenGLManager;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFilament;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;
import pl.AWTGameEngine.windows.HeadlessWindow;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FilamentPanel extends Panel3D implements PanelObject {

    private final Scene scene;
    private final BaseWindow window;
    private final Camera camera;
    private final PhysXManager physXManager;

    public FilamentPanel(Scene scene) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.physXManager = PhysXManager.getInstance();
        physXManager.createScene(scene);
        if(!(window instanceof HeadlessWindow)) {
            graphicsManager3D = new GraphicsManagerFilament();
        }
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
    }

    @Override
    public Component add(Component comp) {
        return null;
    }

    @Override
    public BaseWindow getWindow() {
        return this.window;
    }

    @Override
    public Camera getCamera() {
        return this.camera;
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

    }

    @Override
    public void unload() {
        PhysXManager.getInstance().removeScene(scene);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(getWindow().getWidth(), getWindow().getHeight());
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

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
    }

    @Override
    public void setPreferredSize(Dimension dimension) {

    }

}