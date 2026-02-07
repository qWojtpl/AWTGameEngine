package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.OpenGLInitializer;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PanelGL extends Panel implements PanelObject {

    private final Scene scene;
    private final Window window;
    private final Camera camera;
    private GraphicsManager3D graphicsManager3D;
    private final PhysXManager physXManager;
    private final BufferedImage printBuffer;
    private GLProfile profile;
    private GLCapabilities capabilities;
    private GLCanvas gljPanel;

    public PanelGL(Scene scene, int width, int height) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.physXManager = PhysXManager.getInstance();
        physXManager.createScene(scene);
        if(!window.isServerWindow()) {
            this.graphicsManager3D = new GraphicsManagerGL(this);
            initOpenGL(width, height);
        }
        printBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
    public Window getWindow() {
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

    public GLCanvas getGljPanel() {
        return this.gljPanel;
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager3D == null) {
            return;
        }
        gljPanel.display();
    }

    @Override
    public void updatePhysics() {
        if(getWindow().getCurrentScene() == null) {
            return;
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsPreUpdate")) {
            component.onPhysicsPreUpdate();
        }

        physXManager.simulateFrame(scene, getWindow().getPhysicsLoop().getTargetFps());

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsUpdate")) {
            component.onPhysicsUpdate();
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsAfterUpdate")) {
            component.onPhysicsAfterUpdate();
        }
    }

    @Override
    public void unload() {
        PhysXManager.getInstance().removeScene(scene);
        window.remove(gljPanel);
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

    private void initOpenGL(int width, int height) {
        Logger.info("Initializing OpenGL...");
        profile = GLProfile.get(GLProfile.GL4bc);
        capabilities = new GLCapabilities(profile);
        capabilities.setDepthBits(24);
        gljPanel = new GLCanvas(capabilities);
        gljPanel.setSize(width, height);
        gljPanel.addGLEventListener(new OpenGLInitializer(scene, camera, profile, (GraphicsManagerGL) graphicsManager3D));
        gljPanel.setFocusable(false);
        Logger.info("OpenGL initialized.");
    }

    @Override
    public void setSize(Dimension dimension) {
//        super.setSize(dimension);
        if(gljPanel != null) {
            gljPanel.setSize(dimension);
        }
    }

    @Override
    public void setPreferredSize(Dimension dimension) {

    }

    public GLProfile getGlProfile() {
        return this.profile;
    }

}