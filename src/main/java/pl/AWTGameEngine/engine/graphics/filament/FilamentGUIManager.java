package pl.AWTGameEngine.engine.graphics.filament;

import io.github.erkko68.filament.*;
import io.github.erkko68.filament.filamat.MaterialBuilder;
import io.github.erkko68.filament.filamat.MaterialPackage;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

// todo: dispose gui material
public class FilamentGUIManager {

    private int guiEntity = -1;
    private VertexBuffer guiVertexBuffer;
    private IndexBuffer guiIndexBuffer;
    private MaterialInstance guiMaterialInstance;
    private Texture guiTexture;
    private byte[] guiPixelBuffer;
    private IntBuffer guiIntView;
    private int guiTexWidth = -1;
    private int guiTexHeight = -1;

    private BufferedImage guiImage;
    private Engine engine;
    private View guiView;
    private Scene guiScene;

    public void initGUIView(Engine engine, int width, int height) {
        this.engine = engine;
        this.guiScene = engine.createScene();
        this.guiView = engine.createView();
        Camera guiCamera = engine.createCamera(engine.getEntityManager().create());
        guiCamera.setProjection(io.github.erkko68.filament.Camera.Projection.ORTHO, -1, 1, -1, 1, 0, 1);
        guiView.setCamera(guiCamera);
        guiView.setScene(guiScene);
        guiView.setViewport(new Viewport(0, 0, width, height));
        guiView.setShadowingEnabled(false);
        guiView.setPostProcessingEnabled(false);
        guiView.setBlendMode(View.BlendMode.TRANSLUCENT);
        createGUI();
    }

    public void setGUIImage(BufferedImage bufferedImage) {
        this.guiImage = bufferedImage;
        updateGUI();
    }

    public View getGUIView() {
        return this.guiView;
    }

    private void createGUI() {
        MaterialBuilder materialBuilder = new MaterialBuilder()
                .name("GUIMaterial")
                .culling(MaterialBuilder.CullingMode.NONE)
                .platform(MaterialBuilder.Platform.DESKTOP)
                .targetApi(MaterialBuilder.TargetApi.ALL)
                .require(VertexBuffer.VertexAttribute.UV0)
                .samplerParameter(
                        MaterialBuilder.SamplerType.SAMPLER_2D,
                        MaterialBuilder.SamplerFormat.FLOAT,
                        MaterialBuilder.ParameterPrecision.HIGH,
                        "guiTexture"
                )
                .blending(MaterialBuilder.BlendingMode.TRANSPARENT)
                .shading(MaterialBuilder.Shading.UNLIT)
                .depthWrite(false)
                .depthCulling(false)
                .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material);
                    vec2 uv = getUV0();
                    vec4 color = texture(materialParams_guiTexture, uv);
                    material.baseColor = color.bgra;
                }
                """);

        MaterialPackage materialPackage = materialBuilder.build();
        if (!materialPackage.isValid()) {
            throw new IllegalStateException("Cannot compile material: GuiMaterial");
        }

        guiMaterialInstance = new Material.Builder()
                .payload(materialPackage.getBuffer())
                .build(engine)
                .createInstance();

        float[] vertices = {
                -1.0f, -1.0f, 0.0f,   0.0f, 0.0f,
                1.0f, -1.0f, 0.0f,   1.0f, 0.0f,
                1.0f,  1.0f, 0.0f,   1.0f, 1.0f,
                -1.0f,  1.0f, 0.0f,   0.0f, 1.0f
        };

        byte[] vertexData = new byte[vertices.length * Float.BYTES];
        ByteBuffer.wrap(vertexData)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);

        int stride = 5 * Float.BYTES;
        guiVertexBuffer = new VertexBuffer.Builder()
                .vertexCount(4)
                .bufferCount(1)
                .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, stride)
                .attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 3 * Float.BYTES, stride)
                .build(engine);
        guiVertexBuffer.setBufferAt(engine, 0, vertexData);

        short[] indices = { 0, 1, 2, 2, 3, 0 };
        byte[] indexData = new byte[indices.length * Short.BYTES];
        ByteBuffer.wrap(indexData)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indices);

        guiIndexBuffer = new IndexBuffer.Builder()
                .indexCount(6)
                .bufferType(IndexBuffer.Builder.IndexType.USHORT)
                .build(engine);
        guiIndexBuffer.setBuffer(engine, indexData);

        guiEntity = engine.getEntityManager().create();
        new RenderableManager.Builder(1)
                .boundingBox(new Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, guiVertexBuffer, guiIndexBuffer, 0, 6)
                .material(0, guiMaterialInstance)
                .culling(false)
                .castShadows(false)
                .receiveShadows(false)
                .build(engine, guiEntity);

        guiScene.addEntity(guiEntity);
    }

    private void updateGUI() {
        if(guiImage == null || guiMaterialInstance == null) {
            return;
        }

        int width = guiImage.getWidth();
        int height = guiImage.getHeight();

        if(guiTexture == null || guiTexWidth != width || guiTexHeight != height) {
            if(guiTexture != null) {
                engine.destroyTexture(guiTexture);
            }
            guiTexWidth = width;
            guiTexHeight = height;
            guiPixelBuffer = new byte[width * height * 4];
            guiIntView = ByteBuffer.wrap(guiPixelBuffer)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer();

            guiTexture = new Texture.Builder()
                    .width(width)
                    .height(height)
                    .levels(1)
                    .sampler(Texture.Sampler.SAMPLER_2D)
                    .format(Texture.InternalFormat.RGBA8)
                    .build(engine);

            TextureSampler sampler = new TextureSampler(
                    TextureSampler.MinFilter.LINEAR,
                    TextureSampler.MagFilter.LINEAR,
                    TextureSampler.WrapMode.CLAMP_TO_EDGE
            );
            guiMaterialInstance.setParameter("guiTexture", guiTexture, sampler);
        }

        if(guiImage.getRaster().getDataBuffer() instanceof java.awt.image.DataBufferInt dataBufferInt) {
            int[] pixels = dataBufferInt.getData();
            guiIntView.rewind();
            guiIntView.put(pixels);
        } else {
            int[] pixels = guiImage.getRGB(0, 0, width, height, null, 0, width);
            guiIntView.rewind();
            guiIntView.put(pixels);
        }

        Texture.PixelBufferDescriptor descriptor = new Texture.PixelBufferDescriptor(
                guiPixelBuffer,
                guiPixelBuffer.length,
                Texture.Format.RGBA,
                Texture.Type.UBYTE,
                1, 0, 0, 0, null
        );
        guiTexture.setImage(engine, 0, descriptor);
    }

}
