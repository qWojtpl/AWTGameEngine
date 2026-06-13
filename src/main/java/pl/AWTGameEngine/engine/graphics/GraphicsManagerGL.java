package pl.AWTGameEngine.engine.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.ObjLoader;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Shape;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GraphicsManagerGL extends GraphicsManager3D {

    private final PanelGL panelGL;
    private final ConcurrentHashMap<String, RenderOptions3D> renderables = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Sprite, Texture> textures = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Texture> alphaTextures = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, Shape> shapes = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Sprite> texturesToDelete = new ConcurrentLinkedQueue<>();

    public GraphicsManagerGL(PanelGL panelGL) {
        this.panelGL = panelGL;
    }

    public void initShape(String path, GL4 gl) {

        float[] vertices;
        try {
             vertices = ObjLoader.getVertices(path);
        } catch(Exception e) {
            Logger.exception("Exception while getting vertices of " + path, e);
            return;
        }

        int[] tmp = new int[1];

        gl.glGenVertexArrays(1, tmp, 0);
        int vao = tmp[0];
        gl.glBindVertexArray(vao);

        gl.glGenBuffers(1, tmp, 0);
        int vbo = tmp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES,
                FloatBuffer.wrap(vertices), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 5 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        gl.glBindVertexArray(0);

        shapes.put(path, new Shape(path, vao, vbo, vertices.length));
    }

    public void drawScene(GL4 gl, float[] viewProj) {

        List<RenderOptions3D> renderableList = new ArrayList<>(renderables.values());
        renderableList.sort(Comparator.comparing(RenderOptions3D::isXrayRender));

        List<RenderOptions3D> transparentRenders = new ArrayList<>();

        for(RenderOptions3D ro : renderableList) {
            if(ro.getSprite() != null) {
                if(alphaTextures.contains(textures.get(ro.getSprite()))) {
                    transparentRenders.add(ro);
                    continue;
                }
            }
            drawShape(gl, ro, viewProj);
        }

        for(RenderOptions3D ro : transparentRenders) {
            drawShape(gl, ro, viewProj);
        }

        gl.glBindVertexArray(0);
    }

    private void freeTextures(GL4 gl) {

        if(!texturesToDelete.isEmpty()) {
            List<Sprite> ttd = new ArrayList<>(texturesToDelete);
            for(Sprite s : ttd) {
                boolean remove = false;
                for(RenderOptions3D ro : renderables.values()) {
                    if(s.getImagePath().equals(ro.getSprite().getImagePath())) {
                        remove = true;
                        break;
                    }
                }
                if(remove) {
                    texturesToDelete.remove(s);
                }
            }
        }

        if(!texturesToDelete.isEmpty()) {
            Logger.info("Freeing (" + texturesToDelete.size() + ") textures...");
            for(Sprite sprite : texturesToDelete) {
                if(!textures.containsKey(sprite)) {
                    continue;
                }
                alphaTextures.remove(textures.get(sprite));
                textures.get(sprite).destroy(gl);
                textures.remove(sprite);
                Dependencies.getResourceManager().releaseSpriteResource(sprite.getImagePath());
            }

            texturesToDelete.clear();
        }
    }

    private void drawShape(GL4 gl, RenderOptions3D ro, float[] viewProj) {
        if(ro.getShapePath() == null) {
            return;
        }

        if(!shapes.containsKey(ro.getShapePath())) {
            initShape(ro.getShapePath(), gl);
        }

        Shape shape = shapes.get(ro.getShapePath());

        gl.glBindVertexArray(shape.getVao());

        float[] model = MatrixHelper.composeModelMatrix(
                ro.getPosition(),
                ro.getQuaternionRotation(),
                ro.getSize()
        );

        if(ro.isXrayRender()) {
            int xray = panelGL.getManager().getProgram(gl, "shaders/xray");
            gl.glUseProgram(xray);
            gl.glUniformMatrix4fv(gl.glGetUniformLocation(xray, "viewProj"), 1, false, viewProj, 0);
            gl.glUniformMatrix4fv(gl.glGetUniformLocation(xray, "model"), 1, false, model, 0);
            gl.glDepthFunc(GL4.GL_GREATER);
            gl.glDepthMask(false);
            gl.glDrawArrays(GL4.GL_TRIANGLES, 0, shape.getVertexCount());
            gl.glDepthFunc(GL4.GL_LESS);
            gl.glDepthMask(true);
        }

        int program = panelGL.getManager().getProgram(gl, ro.getShader());

        gl.glUseProgram(program);

        int modelLoc = gl.glGetUniformLocation(program, "model");
        int vpLoc = gl.glGetUniformLocation(program, "viewProj");

        gl.glUniformMatrix4fv(vpLoc, 1, false, viewProj, 0);
        gl.glUniformMatrix4fv(modelLoc, 1, false, model, 0);

        if(ro.getSprite() != null) {
            if(textures.getOrDefault(ro.getSprite(), null) == null) {
                createTexture(gl, ro.getSprite());
            }
            textures.get(ro.getSprite()).bind(gl);
        }

        gl.glDrawArrays(GL.GL_TRIANGLES, 0, shape.getVertexCount());
    }

    @Override
    public void createRenderable(RenderOptions3D options) {
        if(renderables.containsKey(options.getIdentifier())) {
            return;
        }
        renderables.put(options.getIdentifier(), options);
    }

    @Override
    public void removeRenderable(String identifier) {
        updateSprite(identifier, null, true);
        renderables.remove(identifier);
    }

    @Override
    public void updatePosition(String identifier, TransformSet position) {
        renderables.get(identifier).setPosition(position);
    }

    @Override
    public void updateSize(String identifier, TransformSet scale) {
        renderables.get(identifier).setSize(scale);
    }

    @Override
    public void updateRotation(String identifier, TransformSet rotation, QuaternionTransformSet quaternionRotation) {
        renderables.get(identifier).setRotation(rotation);
        renderables.get(identifier).setQuaternionRotation(quaternionRotation);
    }

    @Override
    public void updateSprite(String identifier, Sprite sprite, boolean releaseOldTexture) {
        if(releaseOldTexture) {
            Sprite oldSprite = renderables.get(identifier).getSprite();
            if(oldSprite != null) {
                texturesToDelete.add(oldSprite);
            }
        }
        renderables.get(identifier).setSprite(sprite);
    }

    @Override
    public void updateShader(String identifier, String shader) {
        renderables.get(identifier).setShader(shader);
    }

    @Override
    public void updateShapePath(String identifier, String shapePath) {
        renderables.get(identifier).setShapePath(shapePath);
    }

    @Override
    public void updateColor(String identifier, ColorObject color) {
        renderables.get(identifier).setColor(color);
    }

    @Override
    public void updateXray(String identifier, boolean xray) {
        renderables.get(identifier).setXrayRender(xray);
    }

    public void createTexture(GL4 gl, Sprite sprite) {
        Logger.info("Initializing texture from sprite " + sprite.getImagePath());
        Texture texture = AWTTextureIO.newTexture(panelGL.getGlProfile(), sprite.getImage(), true);
        textures.put(sprite, texture);
        if(sprite.isTransparent()) {
            alphaTextures.add(texture);
        }
        if(gl != null) {
            freeTextures(gl);
        }
    }

}