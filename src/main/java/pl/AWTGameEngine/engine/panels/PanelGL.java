package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyEvent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.util.LinkedHashMap;
import java.util.List;

public class PanelGL extends JFXPanel implements PanelObject, GLEventListener {

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
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        for (int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if(!go.isActive()) {
                    continue;
                }
                if(this.equals(go.getPanel())) {
                    go.render3D(graphicsManager3D);
                }
            }
        }
        canvas.display();
    }

    @Override
    public void updatePhysics() {
        physXManager.getPxScene().simulate(1f/((float) getWindow().getPhysicsLoop().getFPS() / 6));
        physXManager.getPxScene().fetchResults(true);
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        for (int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if(!go.isActive()) {
                    continue;
                }
                if(this.equals(go.getPanel())) {
                    go.updatePhysics();
                }
            }
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
        profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.setSize(width, height);
        add(canvas);
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
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glColor3f(1f, 0f, 0f);
        gl.glVertex2f(-0.5f, -0.5f);
        gl.glVertex2f(0.5f, -0.5f);
        gl.glVertex2f(0f, 0.5f);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
    }
}