package pl.AWTGameEngine.engine.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GraphicsManagerGL extends GraphicsManager3D {

    private final PanelGL panelGL;
    private final ConcurrentHashMap<String, RenderOptions> renderables = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Sprite, Texture> textures = new ConcurrentHashMap<>();
    private int vao;
    private int vbo;

    public GraphicsManagerGL(PanelGL panelGL) {
        this.panelGL = panelGL;
    }

    public void init(GL4 gl) {
        float[] vertices = {
                // pos              // uv
                // front
                -1, -1,  1,  0, 1,
                1, -1,  1,  1, 1,
                1,  1,  1,  1, 0,
                -1, -1,  1,  0, 1,
                1,  1,  1,  1, 0,
                -1,  1,  1,  0, 0,

                // back
                -1, -1, -1,  1, 1,
                -1,  1, -1,  1, 0,
                1,  1, -1,  0, 0,
                -1, -1, -1,  1, 1,
                1,  1, -1,  0, 0,
                1, -1, -1,  0, 1,

                // left
                -1, -1, -1,  0, 1,
                -1, -1,  1,  1, 1,
                -1,  1,  1,  1, 0,
                -1, -1, -1,  0, 1,
                -1,  1,  1,  1, 0,
                -1,  1, -1,  0, 0,

                // right
                1, -1, -1,  1, 1,
                1,  1, -1,  1, 0,
                1,  1,  1,  0, 0,
                1, -1, -1,  1, 1,
                1,  1,  1,  0, 0,
                1, -1,  1,  0, 1,

                // top
                -1,  1, -1,  0, 0,
                -1,  1,  1,  0, 1,
                1,  1,  1,  1, 1,
                -1,  1, -1,  0, 0,
                1,  1,  1,  1, 1,
                1,  1, -1,  1, 0,

                // bottom
                -1, -1, -1,  1, 0,
                1, -1, -1,  0, 0,
                1, -1,  1,  0, 1,
                -1, -1, -1,  1, 0,
                1, -1,  1,  0, 1,
                -1, -1,  1,  1, 1
        };

        int[] tmp = new int[1];

        gl.glGenVertexArrays(1, tmp, 0);
        vao = tmp[0];
        gl.glBindVertexArray(vao);

        gl.glGenBuffers(1, tmp, 0);
        vbo = tmp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * Float.BYTES,
                FloatBuffer.wrap(vertices), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 5 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        gl.glBindVertexArray(0);
    }

    public void drawScene(GL4 gl, int program, float[] viewProj) {

        gl.glBindVertexArray(vao);

        int modelLoc = gl.glGetUniformLocation(program, "model");
        int vpLoc = gl.glGetUniformLocation(program, "viewProj");

        gl.glUniformMatrix4fv(vpLoc, 1, false, viewProj, 0);

        List<RenderOptions> renderableList = new ArrayList<>(renderables.values());
        for (RenderOptions ro : renderableList) {

            float[] model = MatrixHelper.composeModelMatrix(
                    ro.getPosition(),
                    ro.getQuaternionRotation(),
                    ro.getSize()
            );

            gl.glUniformMatrix4fv(modelLoc, 1, false, model, 0);

            if (ro.getSprite() != null) {
                if(textures.getOrDefault(ro.getSprite(), null) == null) {
                    createTexture(ro.getSprite());
                }
                textures.get(ro.getSprite()).bind(gl);
            }

            gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
        }

        gl.glBindVertexArray(0);
    }

    @Override
    public void createBox(RenderOptions options) {
        if(renderables.containsKey(options.getIdentifier())) {
            return;
        }
        renderables.put(options.getIdentifier(), options);
    }

    @Override
    public void removeBox(String identifier) {

    }

    @Override
    public void createSphere(RenderOptions options) {
        if(renderables.containsKey(options.getIdentifier())) {
            return;
        }
        renderables.put(options.getIdentifier(), options);
    }

    @Override
    public void removeSphere(String identifier) {

    }

    @Override
    public void createCylinder(RenderOptions options) {

    }

    @Override
    public void removeCylinder(String identifier) {

    }

    @Override
    public void createCustomModel(RenderOptions options, String modelPath) {

    }

    @Override
    public void removeCustomModel(String identifier) {

    }

    @Override
    public void updatePosition(String identifier, ShapeType shape, TransformSet position) {
        renderables.get(identifier).setPosition(position);
    }

    @Override
    public void updateSize(String identifier, ShapeType shape, TransformSet scale) {
        renderables.get(identifier).setSize(scale);
    }

    @Override
    public void updateRotation(String identifier, ShapeType shape, TransformSet rotation, QuaternionTransformSet quaternionRotation) {
        renderables.get(identifier).setRotation(rotation);
        renderables.get(identifier).setQuaternionRotation(quaternionRotation);
    }

    @Override
    public void updateSprite(String identifier, ShapeType shape, Sprite sprite) {

    }

    @Override
    public void updateColor(String identifier, ShapeType shape, ColorObject color) {
        renderables.get(identifier).setColor(color);
    }

    public void createTexture(Sprite sprite) {
        Logger.info("Initializing texture from sprite " + sprite.getImagePath());
        textures.put(sprite, AWTTextureIO.newTexture(panelGL.getGlProfile(), sprite.getImage(), true));
    }

}