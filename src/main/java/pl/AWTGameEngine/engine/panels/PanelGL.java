package pl.AWTGameEngine.engine.panels;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import javafx.embed.swing.JFXPanel;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.List;

public class PanelGL extends JFXPanel implements PanelObject {

    private final Window window;
    private final Camera camera;
    private final GraphicsManager3D graphicsManager3D;
    private final PhysXManager physXManager;
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
        physXManager.getPxScene().simulate(1f/((float) getWindow().getPhysicsLoop().getFPS() / 6));
        physXManager.getPxScene().fetchResults(true);

        for(GameObject go : getWindow().getCurrentScene().getGameObjects()) {
            go.updatePhysics();
        }
    }

    @Override
    public void unload() {
        physXManager.cleanup();
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

    private void initOpenGL(int width, int height) {
        Logger.log(0, "Initializing OpenGL...");
        profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.setSize(width, height);
        canvas.addGLEventListener(new GLEventListener() {

            private final GLU glu = new GLU();
            private float angle = 0.0f;

            @Override
            public void init(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();

                gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
                gl.glEnable(GL.GL_DEPTH_TEST);
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

                for(GameObject go : getWindow().getCurrentScene().getGameObjects()) {
                    go.render3D(graphicsManager3D);
                }

                final GL2 gl = drawable.getGL().getGL2();

                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
                gl.glLoadIdentity();

                double[] lookAt = RotationHelper.rotationToVectorLookAt(
                        getCamera().getX(), getCamera().getY(), getCamera().getZ(),
                        getCamera().getRotation().getX(),
                        getCamera().getRotation().getY(),
                        getCamera().getRotation().getZ());
                glu.gluLookAt(getCamera().getX(), getCamera().getY(), -60, lookAt[0], lookAt[1], lookAt[2], 0, 1, 0);

                ((GraphicsManagerGL) graphicsManager3D).drawScene(gl);

                angle += 0.5f;
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                final GL2 gl = drawable.getGL().getGL2();
                if (height <= 0) height = 1;
                final float aspect = (float) width / height;

                gl.glMatrixMode(GL2.GL_PROJECTION);
                gl.glLoadIdentity();
                glu.gluPerspective(30, aspect, 1.0, 100.0);

                gl.glMatrixMode(GL2.GL_MODELVIEW);
                gl.glLoadIdentity();
            }
        });
        add(canvas);
        Logger.log(0, "OpenGL initialized.");
    }

    private void initListeners() {
        setMouseListener(new MouseListener(this));
//        fxScene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
//            getWindow().getKeyListener().asKeyPress(event.getCode().getCode());
//        });
//        fxScene.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
//            getWindow().getKeyListener().asKeyType(event.getCode().getCode());
//        });
//        fxScene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
//            getWindow().getKeyListener().asKeyRelease(event.getCode().getCode());
//        });
    }

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        canvas.setSize(dimension);
    }

}