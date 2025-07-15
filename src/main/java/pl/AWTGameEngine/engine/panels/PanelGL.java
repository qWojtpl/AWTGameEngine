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
            GL2 gl;
            float angle = 0.0f;
            int[] vao = new int[1];
            int[] vbo = new int[1];

            @Override
            public void init(GLAutoDrawable drawable) {
                gl = drawable.getGL().getGL2();
                gl.glEnable(GL.GL_DEPTH_TEST);

                float[] vertices = {
                        // Triangle (X, Y, Z)
                        0.0f,  0.5f, 0.0f,
                        -0.5f, -0.5f, 0.0f,
                        0.5f, -0.5f, 0.0f,

                        // Platform (square)
                        -1.0f, -1.0f, -0.5f,
                        1.0f, -1.0f, -0.5f,
                        1.0f, -1.0f,  0.5f,
                        -1.0f, -1.0f,  0.5f
                };

                gl.glGenVertexArrays(1, vao, 0);
                gl.glBindVertexArray(vao[0]);

                gl.glGenBuffers(1, vbo, 0);
                gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
                FloatBuffer buffer = Buffers.newDirectFloatBuffer(vertices);
                gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, buffer, GL.GL_STATIC_DRAW);

                gl.glEnableVertexAttribArray(0);
                gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);

                gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
                gl.glDeleteBuffers(1, vbo, 0);
                gl.glDeleteVertexArrays(1, vao, 0);
            }

            @Override
            public void display(GLAutoDrawable drawable) {

                for(GameObject go : getWindow().getCurrentScene().getGameObjects()) {
                    go.render3D(graphicsManager3D);
                }

                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

                gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
                gl.glLoadIdentity();
                GLU glu = new GLU();
                glu.gluPerspective(45.0, 16.0 / 9.0, 0.1, 100.0);

                gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
                gl.glLoadIdentity();
                glu.gluLookAt(0, 0, 3, 0, 0, 0, 0, 1, 0);

                gl.glBindVertexArray(vao[0]);

                // Draw rotating triangle
                gl.glPushMatrix();
                gl.glTranslatef(0.0f, 0.0f, 0.0f);
                gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3);
                gl.glPopMatrix();

                // Draw platform
                gl.glPushMatrix();
                gl.glColor3f(0.5f, 0.5f, 0.5f);
                gl.glTranslatef(0.0f, -0.5f, 0.0f);
                gl.glBegin(GL2GL3.GL_QUADS);
                gl.glVertex3f(-1.0f, -1.0f, -0.5f);
                gl.glVertex3f(1.0f, -1.0f, -0.5f);
                gl.glVertex3f(1.0f, -1.0f, 0.5f);
                gl.glVertex3f(-1.0f, -1.0f, 0.5f);
                gl.glEnd();
                gl.glPopMatrix();

                angle += 0.5f;
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                gl.glViewport(0, 0, width, height);
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