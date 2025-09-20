package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
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
        this.physXManager = new PhysXManager();
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

        physXManager.simulateFrame(getWindow().getPhysicsLoop().getFPS());

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
        canvas.addGLEventListener(new GLEventListener() {

            private final GLU glu = new GLU();

            @Override
            public void init(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();

                gl.glClearColor(0.192156863f, 0.337254902f, 0.474509804f, 1.0f);
                gl.glEnable(GL.GL_DEPTH_TEST);

                for(String name : prepareTextures.keySet()) {
                    textures.put(name, AWTTextureIO.newTexture(profile, prepareTextures.get(name).getImage(), true));
                }

                prepareTextures.clear();

                gl.glEnable(GL2.GL_TEXTURE_2D);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                gl.glDepthFunc(GL.GL_LEQUAL);
                gl.glShadeModel(GL2.GL_SMOOTH);
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
            }

            @Override
            public void display(GLAutoDrawable drawable) {

                if(getWindow().getCurrentScene() == null) {
                    return;
                }

                for(ObjectComponent component : getWindow().getCurrentScene().getSceneEventHandler().getComponents("on3DRenderRequest#GraphicsManager3D")) {
                    if(component instanceof Renderable3D) {
                        ((Renderable3D) component).on3DRenderRequest(graphicsManager3D);
                    }
                }

                final GL2 gl = drawable.getGL().getGL2();

                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
                gl.glLoadIdentity();

                double[] lookAt = RotationHelper.rotationToVectorLookAt(
                        getCamera().getX(), getCamera().getY(), getCamera().getZ(),
                        getCamera().getRotation().getX(),
                        getCamera().getRotation().getY(),
                        getCamera().getRotation().getZ());

                glu.gluLookAt(getCamera().getX(), getCamera().getY(), getCamera().getZ() - 450, 0, 0, 0, 0, 1, 0);

                ((GraphicsManagerGL) graphicsManager3D).drawScene(gl);
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                final GL2 gl = drawable.getGL().getGL2();
                if (height <= 0) height = 1;
                final float aspect = (float) width / height;

                gl.glMatrixMode(GL2.GL_PROJECTION);
                gl.glLoadIdentity();
                glu.gluPerspective(30, aspect, 1.0, 10000.0);

                gl.glMatrixMode(GL2.GL_MODELVIEW);
                gl.glLoadIdentity();
            }
        });
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