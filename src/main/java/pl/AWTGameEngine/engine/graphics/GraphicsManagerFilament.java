package pl.AWTGameEngine.engine.graphics;

import io.github.erkko68.filament.*;
import io.github.erkko68.filament.filamat.MaterialBuilder;
import io.github.erkko68.filament.filamat.MaterialPackage;
import pl.AWTGameEngine.engine.deserializers.models.ModelLoader;
import pl.AWTGameEngine.engine.helpers.ImageHelper;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.engine.panels.FilamentPanel;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GraphicsManagerFilament extends GraphicsManager3D {

    private final FilamentPanel panel;
    private final ConcurrentHashMap<String, RenderOptions3D> renderables = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, float[]> preloadedVertices = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> renderablesToRemove = new ConcurrentLinkedQueue<>();

    // Filament
    private final ConcurrentHashMap</* Shape path */String, VertexBuffer> vertexBuffers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap</* Shape path */String, IndexBuffer> indexBuffers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap</* Renderable */String, Integer> entities = new ConcurrentHashMap<>();
    private final ConcurrentHashMap</* Image path */String, Texture> textures = new ConcurrentHashMap<>();
    private final ConcurrentHashMap</* Renderable */String, MaterialInstance> materials = new ConcurrentHashMap<>();

    private Material defaultMaterial;

    public GraphicsManagerFilament(FilamentPanel panel) {
        this.panel = panel;
    }

    public void update(Engine engine, View view) {

        if(defaultMaterial == null) {
            createDefaultMaterial(engine);
        }

        prepareCamera(view);

        String renderableToRemove;
        while((renderableToRemove = renderablesToRemove.poll()) != null) {
            renderables.remove(renderableToRemove);
            if(!entities.containsKey(renderableToRemove)) {
                continue;
            }
            disposeEntity(engine, entities.get(renderableToRemove));
            entities.remove(renderableToRemove);
        }

        List<RenderOptions3D> renderableList = new ArrayList<>(renderables.values().stream()
                .map(RenderOptions3D::clone)
                .toList());

        for(RenderOptions3D ro : renderableList) {
            prepareRenderable(ro, engine, view);
        }
    }

    public void dispose(Engine engine) {
        for(int entity : entities.values()) {
            disposeEntity(engine, entity);
        }
        entities.clear();
        for(VertexBuffer vertexBuffer : vertexBuffers.values()) {
            engine.destroyVertexBuffer(vertexBuffer);
        }
        vertexBuffers.clear();
        for(IndexBuffer indexBuffer : indexBuffers.values()) {
            engine.destroyIndexBuffer(indexBuffer);
        }
        indexBuffers.clear();
        if(defaultMaterial != null) {
            for(MaterialInstance instance : materials.values()) {
                engine.destroyMaterialInstance(instance);
            }
            engine.destroyMaterial(defaultMaterial);
            defaultMaterial = null;
        }
        renderables.clear();
        preloadedVertices.clear();
    }

    public void disposeEntity(Engine engine, int entity) {
        engine.getTransformManager().destroy(entity);
        engine.getRenderableManager().destroy(entity);
        engine.getEntityManager().destroy(entity);
        engine.destroyEntity(entity);
        panel.getFilamentScene().removeEntity(entity);
    }

    private void prepareCamera(View view) {

        double x = panel.getCamera().getX(), y = panel.getCamera().getY(), z = panel.getCamera().getZ();
        Vector3 rot = panel.getCamera().getRotation().clone();
        double rx = rot.getX(), ry = rot.getY(), rz = rot.getZ();

        assert view.getCamera() != null;
        double[] look = RotationHelper.rotationToVectorLookAt(
                x, y, z,
                rx, ry, rz
        );
        view.getCamera().lookAt(x, y, z, look[0], look[1], look[2], 0, 1, 0);
    }

    private void prepareRenderable(RenderOptions3D renderOptions3D, Engine engine, View view) {

        assert view.getScene() != null;

        // Shape
        String shapePath = renderOptions3D.getShapePath();
        if(!vertexBuffers.containsKey(shapePath)) {
            float[] vertices;
            if(preloadedVertices.containsKey(shapePath)) {
                vertices = preloadedVertices.get(shapePath);
                preloadedVertices.remove(shapePath);
            } else {
                vertices = ModelLoader.getVertices(shapePath, true);
            }
            createBuffers(shapePath, vertices, engine);
        }

        // Material

        if(!materials.containsKey(renderOptions3D.getIdentifier())) {
            createMaterial(renderOptions3D, engine);
        }

        if(renderOptions3D.getSprite() != null) {
            if(!textures.containsKey(renderOptions3D.getSprite().getImagePath())) {
                createTexture(renderOptions3D.getSprite(), engine);
            }
            materials.get(renderOptions3D.getIdentifier()).setParameter("albedoTexture", textures.get(renderOptions3D.getSprite().getImagePath()), new TextureSampler());
        }

        // Entity
        if(!entities.containsKey(renderOptions3D.getIdentifier())) {
            int entity = engine.getEntityManager().create();
            entities.put(renderOptions3D.getIdentifier(), entity);
            buildEntity(renderOptions3D, engine, entity);
            view.getScene().addEntity(entity);
            engine.getTransformManager().create(entity);
        }

        // Position
        engine.getTransformManager().setTransform(
                engine.getTransformManager().getInstance(entities.get(renderOptions3D.getIdentifier())),
                MatrixHelper.composeModelMatrix(
                        renderOptions3D.getPosition(),
                        renderOptions3D.getQuaternionRotation(),
                        renderOptions3D.getSize())
        );
    }

    private void createBuffers(String path, float[] vertices, Engine engine) {
        int vertexCount = vertices.length / 8;

        float[] positions = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] uvs = new float[vertexCount * 2];

        for(int i = 0; i < vertexCount; i++) {
            int srcOffset = i * 8;
            positions[i * 3] = vertices[srcOffset];
            positions[i * 3 + 1] = vertices[srcOffset + 1];
            positions[i * 3 + 2] = vertices[srcOffset + 2];
            normals[i * 3] = vertices[srcOffset + 3];
            normals[i * 3 + 1] = vertices[srcOffset + 4];
            normals[i * 3 + 2] = vertices[srcOffset + 5];
            uvs[i * 2] = vertices[srcOffset + 6];
            uvs[i * 2 + 1] = vertices[srcOffset + 7];
        }

        int[] triangles = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            triangles[i] = i;
        }

        SurfaceOrientation surfaceOrientation = new SurfaceOrientation.Builder()
                .vertexCount(vertexCount)
                .positions(positions, 0)
                .normals(normals, 0)
                .uvs(uvs, 0)
                .triangleCount(vertexCount / 3)
                .triangles32(triangles)
                .build();

        float[] tangentsArray = new float[vertexCount * 4];
        surfaceOrientation.getQuatsAsFloat(tangentsArray, vertexCount);

        surfaceOrientation.destroy();

        byte[] vertexData = new byte[vertices.length * Float.BYTES];
        ByteBuffer.wrap(vertexData).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);

        byte[] tangentData = new byte[tangentsArray.length * Float.BYTES];
        ByteBuffer.wrap(tangentData).order(ByteOrder.nativeOrder()).asFloatBuffer().put(tangentsArray);

        VertexBuffer vb = new VertexBuffer.Builder()
                .vertexCount(vertexCount)
                .bufferCount(2)
                .attribute(
                        VertexBuffer.VertexAttribute.POSITION,
                        0,
                        VertexBuffer.AttributeType.FLOAT3,
                        0,
                        8 * Float.BYTES
                )
                .attribute(
                        VertexBuffer.VertexAttribute.UV0,
                        0,
                        VertexBuffer.AttributeType.FLOAT2,
                        6 * Float.BYTES,
                        8 * Float.BYTES
                )
                .attribute(
                        VertexBuffer.VertexAttribute.TANGENTS,
                        1,
                        VertexBuffer.AttributeType.FLOAT4,
                        0,
                        4 * Float.BYTES
                )
                .build(engine);

        vb.setBufferAt(engine, 0, vertexData);
        vb.setBufferAt(engine, 1, tangentData);
        vertexBuffers.put(path, vb);

        byte[] indexData = new byte[vertexCount * Integer.BYTES];
        ByteBuffer.wrap(indexData).order(ByteOrder.nativeOrder()).asIntBuffer().put(triangles);

        IndexBuffer ib = new IndexBuffer.Builder()
                .indexCount(vertexCount)
                .bufferType(IndexBuffer.Builder.IndexType.UINT)
                .build(engine);

        ib.setBuffer(engine, indexData);
        indexBuffers.put(path, ib);
    }

    private void createDefaultMaterial(Engine engine) {
        MaterialBuilder materialBuilder = new MaterialBuilder();

        materialBuilder
                .name("DefaultMaterial")
                .shading(MaterialBuilder.Shading.LIT)
                .culling(MaterialBuilder.CullingMode.NONE)
                .platform(MaterialBuilder.Platform.DESKTOP)
                .targetApi(MaterialBuilder.TargetApi.ALL)
                .require(VertexBuffer.VertexAttribute.UV0)
                .samplerParameter(
                        MaterialBuilder.SamplerType.SAMPLER_2D,
                        MaterialBuilder.SamplerFormat.FLOAT,
                        MaterialBuilder.ParameterPrecision.HIGH,
                        "albedoTexture"
                )
                .material("""
                    void material(inout MaterialInputs material) {
                        prepareMaterial(material);
                        //material.baseColor = vec4(0.0, 0.0, 0.0, 1.0);
                        vec2 uv = getUV0();
                        uv.y = 1.0 - uv.y;
                        material.baseColor = texture(materialParams_albedoTexture, uv);
                    }
                    """);

        MaterialPackage materialPackage = materialBuilder.build();

        if(!materialPackage.isValid()) {
            throw new IllegalStateException("Cannot compile default material");
        }

        defaultMaterial = new Material.Builder()
                .payload(materialPackage.getBuffer())
                .build(engine);
    }

    private void createMaterial(RenderOptions3D renderOptions3D, Engine engine) {
        materials.put(renderOptions3D.getIdentifier(), defaultMaterial.createInstance());
    }

    private void buildEntity(RenderOptions3D renderOptions3D, Engine engine, int entity) {
        RenderableManager.Builder renderableBuilder =
                new RenderableManager.Builder(1);

        renderableBuilder
                .geometry(
                        0,
                        RenderableManager.PrimitiveType.TRIANGLES,
                        vertexBuffers.get(renderOptions3D.getShapePath()),
                        indexBuffers.get(renderOptions3D.getShapePath()),
                        0,
                        vertexBuffers.get(renderOptions3D.getShapePath()).getVertexCount()
                )
                .material(
                        0,
                        materials.get(renderOptions3D.getIdentifier())
                )
                .boundingBox(new Box(0, 0, 0,
                        (float) renderOptions3D.getSize().getX(),
                        (float) renderOptions3D.getSize().getY(),
                        (float) renderOptions3D.getSize().getZ()))
                .culling(false)
                .castShadows(!renderOptions3D.getSize().isEmpty())
                .receiveShadows(!renderOptions3D.getSize().isEmpty());

        renderableBuilder.build(
                engine,
                entity
        );
    }

    @Override
    public void preloadShape(String path) {
        addPreloadedVertices(path, ModelLoader.getVertices(path, true));
    }

    @Override
    public void addPreloadedVertices(String path, float[] vertices) {
        preloadedVertices.put(path, vertices);
    }

    @Override
    public void createRenderable(RenderOptions3D options) {
        renderables.put(options.getIdentifier(), options);
    }

    @Override
    public void removeRenderable(String identifier) {
        renderablesToRemove.add(identifier);
    }

    private void createTexture(Sprite sprite, Engine engine) {
        Texture texture = new Texture.Builder()
                .width(sprite.getImage().getWidth())
                .height(sprite.getImage().getHeight())
                .levels(1)
                .format(Texture.InternalFormat.SRGB8_A8)
                .usage(
                        Texture.Usage.Companion.getSAMPLEABLE() |
                        Texture.Usage.Companion.getCOLOR_ATTACHMENT() |
                        Texture.Usage.Companion.getUPLOADABLE() |
                        Texture.Usage.Companion.getGEN_MIPMAPPABLE())
                .build(engine);

        byte[] byteArray = ImageHelper.bufferedImageToByteArray(sprite.getImage());
        Texture.PixelBufferDescriptor descriptor = new Texture.PixelBufferDescriptor(
                byteArray,
                byteArray.length,
                Texture.Format.RGBA,
                Texture.Type.UBYTE,
                1,
                0,
                0,
                0,
                null
        );
        texture.setImage(engine, 0, descriptor);
        texture.generateMipmaps(engine);

        textures.put(sprite.getImagePath(), texture);
    }

    @Override
    public void freeTexture(RenderOptions3D options) {

    }

    @Override
    public void updateTexture(Sprite sprite) {

    }

    @Override
    public RenderOptions3D getRenderable(String identifier) {
        return null;
    }

}
