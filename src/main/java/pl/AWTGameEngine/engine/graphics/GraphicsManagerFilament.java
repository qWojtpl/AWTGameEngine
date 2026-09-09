package pl.AWTGameEngine.engine.graphics;

import io.github.erkko68.filament.*;
import io.github.erkko68.filament.filamat.MaterialBuilder;
import io.github.erkko68.filament.filamat.MaterialPackage;
import pl.AWTGameEngine.engine.deserializers.models.ModelLoader;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.Vector4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GraphicsManagerFilament extends GraphicsManager3D {

    private final ConcurrentHashMap<String, RenderOptions3D> renderables = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, float[]> preloadedVertices = new ConcurrentHashMap<>();

    // Filament
    private final ConcurrentHashMap<String, VertexBuffer> vertexBuffers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IndexBuffer> indexBuffers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> entities = new ConcurrentHashMap<>();

    private MaterialInstance defaultMaterial;

    public void update(Engine engine, View view) {

        if(defaultMaterial == null) {
            createDefaultMaterial(engine);
        }

        List<RenderOptions3D> renderableList = new ArrayList<>(renderables.values().stream()
                .map(RenderOptions3D::clone)
                .toList());

        for(RenderOptions3D ro : renderableList) {
            prepareRenderable(ro, engine, view);
        }
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

        // Entity
        if(!entities.containsKey(renderOptions3D.getIdentifier())) {
            int entity = engine.getEntityManager().create();
            entities.put(renderOptions3D.getIdentifier(), entity);
            buildEntity(renderOptions3D, engine, entity);
            view.getScene().addEntity(entity);
            engine.getTransformManager().create(entity);
        }

        float[] transformMatrix = MatrixHelper.composeModelMatrix(
                renderOptions3D.getPosition(),
                renderOptions3D.getQuaternionRotation(),
                renderOptions3D.getSize()
        );

        // Position
        engine.getTransformManager().setTransform(
                engine.getTransformManager().getInstance(entities.get(renderOptions3D.getIdentifier())),
                transformMatrix
        );
    }

    private void createBuffers(String path, float[] vertices, Engine engine) {
        int vertexCount = vertices.length / 8;

        byte[] vertexData = new byte[vertices.length * Float.BYTES];
        ByteBuffer byteBuf = ByteBuffer
                .wrap(vertexData)
                .order(ByteOrder.nativeOrder());

        for(float v : vertices) {
            byteBuf.putFloat(v);
        }

        VertexBuffer vb = new VertexBuffer.Builder()
                .vertexCount(vertexCount)
                .bufferCount(1)
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
                .build(engine);

        vb.setBufferAt(engine, 0, vertexData);
        vertexBuffers.put(path, vb);

        byte[] indexData = new byte[vertexCount * Integer.BYTES];

        ByteBuffer indexBuf = ByteBuffer
                .wrap(indexData)
                .order(ByteOrder.nativeOrder());

        for (short i = 0; i < vertexCount; i++) {
            indexBuf.putInt(i);
        }

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
                .shading(MaterialBuilder.Shading.UNLIT)
                .culling(MaterialBuilder.CullingMode.NONE)
                .platform(MaterialBuilder.Platform.DESKTOP)
                .targetApi(MaterialBuilder.TargetApi.VULKAN)
                .material("""
                    void material(inout MaterialInputs material) {
                        prepareMaterial(material);
                        material.baseColor = vec4(1.0, 0.0, 0.0, 1.0);
                    }
                    """);

        MaterialPackage materialPackage = materialBuilder.build();

        if(!materialPackage.isValid()) {
            throw new IllegalStateException("Cannot compile default material");
        }

        Material material = new Material.Builder()
                .payload(materialPackage.getBuffer())
                .build(engine);

        defaultMaterial = material.createInstance();
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
                        defaultMaterial
                )
                .culling(false)
                .castShadows(false)
                .receiveShadows(false);

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
