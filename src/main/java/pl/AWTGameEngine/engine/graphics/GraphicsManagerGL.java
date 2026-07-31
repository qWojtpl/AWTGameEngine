package pl.AWTGameEngine.engine.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.models.ModelLoader;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.engine.helpers.SkyboxHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Shape;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

import java.awt.image.DataBufferInt;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GraphicsManagerGL extends GraphicsManager3D {

    private final PanelGL panelGL;
    private final ConcurrentHashMap<String, RenderOptions3D> renderables = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Sprite, Texture> textures = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Texture> alphaTextures = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, Shape> shapes = new ConcurrentHashMap<>();
    private final Set<Sprite> texturesToDelete = ConcurrentHashMap.newKeySet();
    private final Set<Sprite> texturesToUpdate = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, float[]> preloadedVertices = new ConcurrentHashMap<>();

    private Shape skyboxShape;
    private Texture skyboxTexture;
    private List<Sprite> skyboxSprites;

    public GraphicsManagerGL(PanelGL panelGL) {
        this.panelGL = panelGL;
    }

    public void initShape(String path, GL4 gl) {

        float[] vertices;
        if(preloadedVertices.containsKey(path)) {
            vertices = preloadedVertices.get(path);
            preloadedVertices.remove(path);
        } else {
            try {
                vertices = ModelLoader.getVertices(path, true);
            } catch (Exception e) {
                Logger.exception("Exception while getting vertices of " + path, e);
                return;
            }
        }

        initShape(path, vertices, gl);
    }

    public void initShape(String path, float[] vertices, GL4 gl) {
        int[] tmp = new int[1];

        gl.glGenVertexArrays(1, tmp, 0);
        int vao = tmp[0];
        gl.glBindVertexArray(vao);

        gl.glGenBuffers(1, tmp, 0);
        int vbo = tmp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES,
                FloatBuffer.wrap(vertices), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 8 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        gl.glEnableVertexAttribArray(2);

        gl.glBindVertexArray(0);

        shapes.put(path, new Shape(path, vao, vbo, vertices.length / 8));
        Logger.info("Model " + path + " loaded.");
    }

    public void preloadShape(String path) {
        preloadedVertices.put(path, ModelLoader.getVertices(path, true));
    }

    public void drawScene(GL4 gl, float[] viewProj, float[] skyboxViewProj) {

        for(Sprite s : texturesToUpdate) {
            updateTexture(gl, s);
            texturesToUpdate.remove(s);
        }

        List<RenderOptions3D> renderableList = new ArrayList<>(renderables.values());
        renderableList.sort(Comparator.comparing(RenderOptions3D::isXrayRender));

        List<RenderOptions3D> transparentRenders = new ArrayList<>();

        for(RenderOptions3D ro : renderableList) {
            if(ro.getSprite() != null) {
                if(alphaTextures.contains(textures.get(ro.getSprite())) || ro.getOpacity() != 1) {
                    transparentRenders.add(ro);
                    continue;
                }
            }
            drawShape(gl, ro, viewProj);
        }

        for(RenderOptions3D ro : transparentRenders) {
            drawShape(gl, ro, viewProj);
        }

        drawSkybox(gl, skyboxViewProj);

        gl.glBindVertexArray(0);
    }

    private void freeTextures(GL4 gl) {

        if(!texturesToDelete.isEmpty()) {
            List<Sprite> ttd = new ArrayList<>(texturesToDelete);
            for(Sprite s : ttd) {
                boolean remove = false;
                for(RenderOptions3D ro : renderables.values()) {
                    if(ro.getSprite() == null) {
                        continue;
                    }
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
                texturesToDelete.remove(sprite);
            }
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

        if(shape == null) {
            return;
        }

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
        int opacityLoc = gl.glGetUniformLocation(program, "opacity");

        gl.glUniformMatrix4fv(vpLoc, 1, false, viewProj, 0);
        gl.glUniformMatrix4fv(modelLoc, 1, false, model, 0);

        float opacity = ro.getOpacity();

        if(opacity > 1) {
            opacity = 1;
        }

        gl.glUniform1f(opacityLoc, opacity);

        if(ro.getSprite() != null) {
            if(textures.getOrDefault(ro.getSprite(), null) == null) {
                createTexture(gl, ro.getSprite());
            }
            textures.get(ro.getSprite()).bind(gl);
        }

        gl.glDrawArrays(GL.GL_TRIANGLES, 0, shape.getVertexCount());
    }

    private void drawSkybox(GL4 gl, float[] skyboxViewProj) {
        if(skyboxTexture == null) {
            if(skyboxSprites != null) {
                skyboxTexture = createCubeMap(gl, skyboxSprites);
                skyboxSprites = null;
            } else {
                return;
            }
        }

        if(skyboxShape == null) {
            initShape("$skybox", SkyboxHelper.getSkyboxVertices(), gl);
            skyboxShape = shapes.get("$skybox");
        }

        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glDepthMask(false);
        gl.glDisable(GL.GL_CULL_FACE);
        int shader = panelGL.getManager().getProgram(gl, "shaders/skybox");
        gl.glUseProgram(shader);
        int vpLoc = gl.glGetUniformLocation(shader, "viewProj");
        gl.glUniformMatrix4fv(vpLoc, 1, false, skyboxViewProj, 0);

        skyboxTexture.bind(gl);

        int skyboxLoc = gl.glGetUniformLocation(shader, "skybox");
        gl.glUniform1f(skyboxLoc, 0);

        gl.glBindVertexArray(shapes.get("$skybox").getVao());
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, shapes.get("$skybox").getVertexCount());
        gl.glBindVertexArray(0);

        gl.glDepthMask(true);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_CULL_FACE);
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
    public RenderOptions3D getRenderable(String identifier) {
        return renderables.get(identifier);
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
        if(!renderables.containsKey(identifier)) {
            return;
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

    public void updateTexture(Sprite sprite) {
        if(texturesToUpdate.contains(sprite)) {
            return;
        }
        texturesToUpdate.add(sprite);
    }

    public void updateTexture(GL4 gl, Sprite sprite) {
        if(!textures.containsKey(sprite)) {
            createTexture(gl, sprite);
            return;
        }

        IntBuffer buffer = IntBuffer.wrap(((DataBufferInt) sprite.getImage().getRaster().getDataBuffer()).getData());

        textures.get(sprite).bind(gl);

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);

        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0,
                sprite.getImage().getWidth(),
                sprite.getImage().getHeight(),
                GL.GL_BGRA, GL.GL_UNSIGNED_BYTE,
                buffer
        );
    }

    public void setSkyboxSprites(List<Sprite> sprites) {
        this.skyboxSprites = new ArrayList<>(sprites);
        skyboxTexture = null;
    }

    public Texture createCubeMap(GL gl, List<Sprite> sprites) {
        int[] targets = {
                GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
                GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
                GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        };

        Texture cubemap = AWTTextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);

        cubemap.bind(gl);

        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < targets.length; i++) {
            TextureData data = AWTTextureIO.newTextureData(panelGL.getGlProfile(), sprites.get(i).getImage(), true);
            cubemap.updateImage(gl, data, targets[i]);
        }

        return cubemap;
    }

}