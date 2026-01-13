package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.texture.Texture;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.OpenGLInitializer;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class PanelGL extends JLayeredPane implements PanelObject {

    private final Scene scene;
    private final Window window;
    private final Camera camera;
    private GraphicsManager3D graphicsManager3D;
    private final PhysXManager physXManager;
    private final BufferedImage printBuffer;
    private GLProfile profile;
    private GLCapabilities capabilities;
    private GLJPanel gljPanel;
    private MouseListener mouseListener;

    public PanelGL(Scene scene, int width, int height) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.physXManager = PhysXManager.getInstance();
        if(!window.isServerWindow()) {
            this.graphicsManager3D = new GraphicsManagerGL(this);
            initOpenGL(width, height);
            initListeners();
        }
        printBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
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

    public GLJPanel getGljPanel() {
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

        physXManager.simulateFrame(getWindow().getPhysicsLoop().getTargetFps());

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsUpdate")) {
            component.onPhysicsUpdate();
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsAfterUpdate")) {
            component.onPhysicsAfterUpdate();
        }
    }

    @Override
    public void unload() {
        physXManager.cleanup();
        window.remove(this);
    }

    public void setMouseListener(MouseListener mouseListener) {
        if(gljPanel == null) {
            return;
        }
        if (this.mouseListener != null) {
            gljPanel.removeMouseListener(this.mouseListener);
            gljPanel.removeMouseMotionListener(this.mouseListener);
            gljPanel.removeMouseWheelListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        gljPanel.addMouseListener(mouseListener);
        gljPanel.addMouseMotionListener(mouseListener);
        gljPanel.addMouseWheelListener(mouseListener);
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
        gljPanel = new GLJPanel(capabilities);
        gljPanel.setSize(width, height);
        gljPanel.addGLEventListener(new OpenGLInitializer(scene, camera, profile, (GraphicsManagerGL) graphicsManager3D));
        gljPanel.setFocusable(false);
        add(gljPanel);
        Logger.info("OpenGL initialized.");
    }

    private void initListeners() {
        setMouseListener(new MouseListener(window));
    }

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        if(gljPanel != null) {
            gljPanel.setSize(dimension);
        }
    }

    public GLProfile getGlProfile() {
        return this.profile;
    }

}