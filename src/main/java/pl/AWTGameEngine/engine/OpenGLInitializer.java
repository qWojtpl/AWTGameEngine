package pl.AWTGameEngine.engine;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.windows.Window;

import java.util.HashMap;

public class OpenGLInitializer implements GLEventListener {

    private final Window window;
    private final Camera camera;
    private final GLProfile profile;
    private final GraphicsManagerGL graphicsManagerGL;
    private final HashMap<String, Sprite> prepareTextures;
    private final HashMap<String, Texture> textures;
    private final GLU glu = new GLU();

    public OpenGLInitializer(Window window, Camera camera, GLProfile profile, GraphicsManagerGL graphicsManagerGL, HashMap<String, Sprite> prepareTextures, HashMap<String, Texture> textures) {
        this.window = window;
        this.camera = camera;
        this.profile = profile;
        this.graphicsManagerGL = graphicsManagerGL;
        this.prepareTextures = prepareTextures;
        this.textures = textures;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0.192156863f, 0.337254902f, 0.474509804f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);

        for(String name : prepareTextures.keySet()) {
            textures.put(name, AWTTextureIO.newTexture(profile, prepareTextures.get(name).getImage(), true));
        }

        prepareTextures.clear();

        gl.setSwapInterval(0);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glShadeModel(GL2.GL_SMOOTH);

        Thread.currentThread().setName("RenderLoop-opengl");
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        final GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if(window.getCurrentScene() == null) {
            return;
        }

        for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("on3DRenderRequest#GraphicsManager3D")) {
            component.on3DRenderRequest(graphicsManagerGL);
        }

        double[] lookAt = RotationHelper.rotationToVectorLookAt(
                camera.getX(), camera.getY(), camera.getZ(),
                camera.getRotation().getX(),
                camera.getRotation().getY(),
                camera.getRotation().getZ());

        glu.gluLookAt(camera.getX(), camera.getY(), camera.getZ(), lookAt[0], lookAt[1], lookAt[2], 0, 1.0f, 0);

        graphicsManagerGL.drawScene(gl);
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

}
