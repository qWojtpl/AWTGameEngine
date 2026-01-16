package pl.AWTGameEngine.engine;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.util.HashMap;

public class OpenGLInitializer implements GLEventListener {

    private final Scene scene;
    private final Window window;
    private final Camera camera;
    private final GLProfile profile;
    private final GraphicsManagerGL graphicsManagerGL;
    private int program;

    public OpenGLInitializer(Scene scene, Camera camera, GLProfile profile, GraphicsManagerGL graphicsManagerGL) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = camera;
        this.profile = profile;
        this.graphicsManagerGL = graphicsManagerGL;
    }

    int pixelSizeLoc;
    int resLoc;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        try {
            program = createProgram(gl, "shader");
        } catch(Exception e) {
            Logger.exception("Cannot create GL program", e);
        }

        pixelSizeLoc =gl.glGetUniformLocation(program, "pixelSize");
        resLoc = gl.glGetUniformLocation(program, "resolution");

        gl.glClearColor(0.192156863f, 0.337254902f, 0.474509804f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.setSwapInterval(0);
        gl.glEnable(GL4.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDepthFunc(GL.GL_LEQUAL);

        Thread.currentThread().setName("RenderLoop-opengl");
        graphicsManagerGL.init(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glDeleteProgram(program);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(program);

        for (ObjectComponent c :
                scene.getSceneEventHandler()
                        .getComponents("on3DRenderRequest#GraphicsManager3D")) {
            c.on3DRenderRequest(graphicsManagerGL);
        }

        float[] projection = MatrixHelper.perspective(
                60f,
                window.getWidth() / (float) window.getHeight(),
                0.1f,
                10000f
        );

        float[] view = MatrixHelper.lookAt(camera);
        float[] viewProj = MatrixHelper.mul(projection, view);

        graphicsManagerGL.drawScene(gl, program, viewProj);
        gl.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL4 gl = drawable.getGL().getGL4();
        gl.glViewport(0, 0, width, height);
    }

    private int createProgram(GL4 gl, String shaderName) {
        Logger.info("Compiling shader: " + shaderName + "...");
        int vs = compileShader(gl, GL4.GL_VERTEX_SHADER, getShaderFile(shaderName + ".vert"));
        int fs = compileShader(gl, GL4.GL_FRAGMENT_SHADER, getShaderFile(shaderName + ".frag"));

        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vs);
        gl.glAttachShader(program, fs);
        gl.glLinkProgram(program);

        int[] status = new int[1];
        gl.glGetProgramiv(program, GL4.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            throw new RuntimeException(getProgramLog(gl, program));
        }

        gl.glDeleteShader(vs);
        gl.glDeleteShader(fs);
        return program;
    }

    private String getShaderFile(String fileName) {
        return String.join("\n", Dependencies.getResourceManager().getResource("shaders/" + fileName));
    }

    private int compileShader(GL4 gl, int type, String src) {
        int s = gl.glCreateShader(type);
        gl.glShaderSource(s, 1, new String[]{src}, new int[]{src.length()}, 0);
        gl.glCompileShader(s);

        int[] status = new int[1];
        gl.glGetShaderiv(s, GL4.GL_COMPILE_STATUS, status, 0);
        if(status[0] == 0) {
            throw new RuntimeException(getShaderLog(gl, s));
        }
        return s;
    }

    private String getShaderLog(GL4 gl, int s) {
        byte[] buf = new byte[1024];
        gl.glGetShaderInfoLog(s, buf.length, null, 0, buf, 0);
        return new String(buf);
    }

    private String getProgramLog(GL4 gl, int p) {
        byte[] buf = new byte[1024];
        gl.glGetProgramInfoLog(p, buf.length, null, 0, buf, 0);
        return new String(buf);
    }

}
