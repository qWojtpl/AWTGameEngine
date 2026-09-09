package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import org.jetbrains.annotations.NotNull;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.OpenGLManager;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;
import pl.AWTGameEngine.windows.HeadlessWindow;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;

public class PanelGL extends Panel3D implements PanelObject {

    private final Scene scene;
    private final BaseWindow window;
    private final Camera camera;
    private final PhysXManager physXManager;
    private GLProfile profile;
    private GLCanvas glCanvas;
    private OpenGLManager manager;

    public PanelGL(Scene scene) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.physXManager = PhysXManager.getInstance();
        physXManager.createScene(scene);
        if(!(window instanceof HeadlessWindow)) {
            this.graphicsManager3D = new GraphicsManagerGL(this);
            initOpenGL(getWindow().getBaseWidth(), getWindow().getBaseHeight());
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

    public PhysXManager getPhysXManager() {
        return this.physXManager;
    }

    public GLCanvas getGlCanvas() {
        return this.glCanvas;
    }

    public OpenGLManager getManager() {
        return this.manager;
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager3D == null) {
            return;
        }
        glCanvas.display();
    }

    @Override
    public void unload() {
        PhysXManager.getInstance().removeScene(scene);
        if(!(window instanceof HeadlessWindow)) {
            ((Window) window).remove(glCanvas);
        }
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
        //todo
    }

    @Override
    public void onSceneLoad() {

    }

    private void initOpenGL(int width, int height) {
        Logger.info("Initializing OpenGL...");
        profile = GLProfile.get(GLProfile.GL4bc);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDepthBits(24);
        glCanvas = new GLCanvas(capabilities);
        glCanvas.setSize(width, height);
        manager = new OpenGLManager(scene, camera, (GraphicsManagerGL) graphicsManager3D);
        glCanvas.addGLEventListener(manager);
        glCanvas.setFocusable(false);
        ((Window) window).add(glCanvas);
        Logger.info("OpenGL initialized.");
    }

    @Override
    public void setSize(@NotNull Dimension dimension) {
//        super.setSize(dimension);
        if(glCanvas != null) {
            glCanvas.setSize(dimension);
        }
    }

    @Override
    public void setPreferredSize(Dimension dimension) {

    }

    public GLProfile getGlProfile() {
        return this.profile;
    }

}