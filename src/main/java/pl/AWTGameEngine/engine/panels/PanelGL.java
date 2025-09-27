package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
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
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PanelGL extends JPanel implements PanelObject {

    private final Window window;
    private final Camera camera;
    private final GraphicsManager3D graphicsManager3D;
    private final PhysXManager physXManager;
    private final HashMap<String, Sprite> prepareTextures = new HashMap<>();
    private final HashMap<String, Texture> textures = new HashMap<>();
    private GLProfile profile;
    private GLCapabilities capabilities;
    private GLCanvas canvas;
    private MouseListener mouseListener;

    public PanelGL(Window window, int width, int height) {
        this.window = window;
        this.camera = new Camera(this);
        this.graphicsManager3D = new GraphicsManagerGL(this);
        this.physXManager = PhysXManager.getInstance();
        physXManager.init();
        initOpenGL(width, height);
        initListeners();
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

    public GraphicsManager3D getGraphicsManager3D() {
        return this.graphicsManager3D;
    }

    public PhysXManager getPhysXManager() {
        return this.physXManager;
    }

    public GLCanvas getCanvas() {
        return this.canvas;
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager3D == null) {
            return;
        }
        canvas.display();
    }

    @Override
    public void updatePhysics() {
        if(getWindow().getCurrentScene() == null) {
            return;
        }

        physXManager.simulateFrame(getWindow().getPhysicsLoop().getTargetFps());

        for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onPhysicsUpdate")) {
            component.onPhysicsUpdate();
        }
    }

    @Override
    public void unload() {
        physXManager.cleanup();
    }

    public void setMouseListener(MouseListener mouseListener) {
        if (this.mouseListener != null) {
            canvas.removeMouseListener(this.mouseListener);
            canvas.removeMouseMotionListener(this.mouseListener);
            canvas.removeMouseWheelListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addMouseWheelListener(mouseListener);
    }

    public void prepareTexture(String name, Sprite sprite) {
        prepareTextures.put(name, sprite);
    }

    public Texture getTexture(String name) {
        return textures.getOrDefault(name, null);
    }

    public void submitInit() {
        canvas.setFocusable(false);
        add(canvas);
        Logger.info("OpenGL initialized.");
    }

    private void initOpenGL(int width, int height) {
        Logger.info("Initializing OpenGL...");
        profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.setSize(width, height);
        canvas.addGLEventListener(new OpenGLInitializer(window, camera, profile, (GraphicsManagerGL) graphicsManager3D, prepareTextures, textures));
        Logger.info("Waiting for textures...");
    }

    private void initListeners() {
        setMouseListener(new MouseListener(this));
    }

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        canvas.setSize(dimension);
    }

}