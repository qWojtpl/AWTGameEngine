package pl.AWTGameEngine.engine;

import com.jogamp.opengl.*;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.exceptions.ShaderCompileException;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.transform.Vector3;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.util.*;

public class OpenGLManager implements GLEventListener {

    private final Scene scene;
    private final Window window;
    private final Camera camera;
    private final GraphicsManagerGL graphicsManagerGL;

    public OpenGLManager(Scene scene, Camera camera, GraphicsManagerGL graphicsManagerGL) {
        this.scene = scene;
        this.window = (Window) scene.getWindow();
        this.camera = camera;
        this.graphicsManagerGL = graphicsManagerGL;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClearColor(0.192156863f, 0.337254902f, 0.474509804f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.setSwapInterval(0);
        gl.glEnable(GL4.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDepthFunc(GL.GL_LEQUAL);

        Thread.currentThread().setName("RenderLoop-opengl");
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        Shaders.disposePrograms(window, gl);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        Set<Vector3> locks = new LinkedHashSet<>();

        try {
            for(ObjectComponent c :
                    scene.getSceneEventHandler()
                            .getComponents("on3DRenderRequest#GraphicsManager3D")) {
                if(!locks.contains(c.getObject().getPosition())) {
                    c.getObject().getPosition().lock();
                    locks.add(c.getObject().getPosition());
                }
                c.on3DRenderRequest(graphicsManagerGL);
            }
        } catch(Exception e) {
            Logger.exception("Unhandled exception caught while running an iteration of OpenGL 3D render request", e);
            for(Vector3 locked : locks) {
                locked.unlock();
            }
            return;
        }

        float[] projection = MatrixHelper.perspective(
                60f,
                window.getWidth() / (float) window.getHeight(),
                0.1f,
                10000f
        );

        float[] view = MatrixHelper.lookAt(camera);
        float[] viewProj = MatrixHelper.mul(projection, view);

        float[] skyboxView = view.clone();
        skyboxView[12] = 0;
        skyboxView[13] = 0;
        skyboxView[14] = 0;

        float[] skyboxViewProj = MatrixHelper.mul(projection, skyboxView);

        try {
            graphicsManagerGL.drawScene(gl, viewProj, skyboxViewProj);
        } catch(Exception e) {
            Logger.exception("Unhandled exception caught while drawing a OpenGL scene", e);
        } finally {
            for(Vector3 locked : locks) {
                try {
                    locked.unlock();
                } catch(IllegalMonitorStateException e) {
                    Logger.exception("Failed to unlock transform", e);
                }
            }
            gl.glUseProgram(0);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL4 gl = drawable.getGL().getGL4();
        gl.glViewport(0, 0, width, height);
    }

}
