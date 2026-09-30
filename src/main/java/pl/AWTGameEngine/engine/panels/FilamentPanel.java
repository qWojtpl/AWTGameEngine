package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import io.github.erkko68.filament.*;
import io.github.erkko68.filament.filamat.MaterialBuilder;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFilament;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.transform.Vector3;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;
import pl.AWTGameEngine.windows.HeadlessWindow;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.LinkedHashSet;
import java.util.Set;

public class FilamentPanel extends Panel3D implements PanelObject {

    private final Scene scene;
    private final BaseWindow window;
    private final Camera camera;
    private final PhysXManager physXManager;
    private Canvas canvas;
    private boolean initialized = false;

    private Engine engine;
    private io.github.erkko68.filament.Scene filamentScene;
    private SwapChain swapChain;
    private Renderer renderer;
    private View view;
    private LightManager.ShadowOptions shadowOptions;

    private byte[] pixelBufferArray;
    private Texture.PixelBufferDescriptor pixelBufferDescriptor;
    private BufferedImage reusableImage;
    private int[] imagePixels;
    private int cachedWidth = 0;
    private int cachedHeight = 0;

    public FilamentPanel(Scene scene) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.physXManager = PhysXManager.getInstance();
        physXManager.createScene(scene);
        if(!(window instanceof HeadlessWindow)) {
            graphicsManager3D = new GraphicsManagerFilament(this);
        }
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
    }

    @Override
    public Component add(Component comp) {
        return null;
    }

    @Override
    public BaseWindow getWindow() {
        return this.window;
    }

    @Override
    public Camera getCamera() {
        return this.camera;
    }

    public GraphicsManager3D getGraphicsManager3D() {
        return this.graphicsManager3D;
    }

    public PhysXManager getPhysXManager() {
        return this.physXManager;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public io.github.erkko68.filament.Scene getFilamentScene() {
        return this.filamentScene;
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null || graphicsManager3D == null) {
            return;
        }

        if(!initialized) {
            initFilament();
            initialized = true;
        }

        ensureBuffersSize(window.getBaseWidth(), window.getBaseHeight());

        Set<Vector3> locks = new LinkedHashSet<>();
        try {
            for(ObjectComponent c :
                    scene.getSceneEventHandler()
                            .getComponents("on3DRenderRequest#GraphicsManager3D")) {
                if(!locks.contains(c.getObject().getPosition())) {
                    c.getObject().getPosition().lock();
                    locks.add(c.getObject().getPosition());
                }
                c.on3DRenderRequest(graphicsManager3D);
            }
            ((GraphicsManagerFilament) graphicsManager3D).update(engine, view);
            if(renderer.beginFrame(swapChain, System.nanoTime())) {
                renderer.render(view);

                renderer.readPixels(0, 0, window.getBaseWidth(), window.getBaseHeight(), pixelBufferDescriptor);

                renderer.endFrame();
                engine.flush();

                int ptr = 0;
                int totalPixels = window.getBaseWidth() * window.getBaseHeight();
                for(int i = 0; i < totalPixels; i++) {
                    int r = pixelBufferArray[ptr++] & 0xFF;
                    int g = pixelBufferArray[ptr++] & 0xFF;
                    int b = pixelBufferArray[ptr++] & 0xFF;
                    int a = pixelBufferArray[ptr++] & 0xFF;

                    imagePixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
                }

                Graphics2D g2d = reusableImage.createGraphics();
                // draw
                g2d.dispose();

                if(canvas != null && canvas.isDisplayable()) {
                    Graphics g = canvas.getGraphics();
                    if(g != null) {
                        g.drawImage(reusableImage, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
                        g.dispose();
                    }
                }
            }
        } catch(Exception e) {
            Logger.exception("Unhandled exception caught while running an iteration of Filament render request", e);
        } finally {
            for(Vector3 locked : locks) {
                try {
                    locked.unlock();
                } catch(IllegalMonitorStateException e) {
                    Logger.exception("Failed to unlock Vector3", e);
                }
            }
        }
    }

    private void ensureBuffersSize(int width, int height) {
        if(width == cachedWidth && height == cachedHeight && reusableImage != null) {
            return;
        }

        this.cachedWidth = width;
        this.cachedHeight = height;

        int sizeInBytes = width * height * 4;
        this.pixelBufferArray = new byte[sizeInBytes];

        this.pixelBufferDescriptor = new Texture.PixelBufferDescriptor(
                pixelBufferArray,
                sizeInBytes,
                Texture.Format.RGBA,
                Texture.Type.UBYTE,
                1, 0, 0, 0,
                null
        );

        this.reusableImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.imagePixels = ((DataBufferInt) reusableImage.getRaster().getDataBuffer()).getData();
    }

    @Override
    public void unload() {
        PhysXManager.getInstance().removeScene(scene);
        if(!(window instanceof HeadlessWindow)) {
            ((Window) window).remove(canvas);
            window.getRenderLoop().addNextFrameOperation(() -> {
                ((GraphicsManagerFilament) graphicsManager3D).dispose(engine);
                engine.destroyView(view);
                engine.destroyScene(filamentScene);
                engine.destroySwapChain(swapChain);
                engine.destroyRenderer(renderer);
                engine.destroy();
            });
        }
    }

    @Override
    public Dimension getSize() {
        return new Dimension(getWindow().getWidth(), getWindow().getHeight());
    }

    @Override
    public void setCursor(Cursor cursor) {

    }

    @Override
    public void setOpaque(boolean opaque) {

    }

    @Override
    public void printToGraphics(Graphics2D g) {

    }

    @Override
    public void onSceneLoad() {

    }

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
    }

    @Override
    public void setPreferredSize(Dimension dimension) {

    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public LightManager.ShadowOptions getShadowOptions() {
        if (this.shadowOptions == null) {
            this.shadowOptions = new LightManager.ShadowOptions();
            shadowOptions.setMapSize(2048);
            shadowOptions.setShadowCascades(4);
            shadowOptions.setBlurWidth(0);
        }
        return this.shadowOptions;
    }

    private void initFilament() {
        this.canvas = new Canvas();
        canvas.setFocusable(false);
        canvas.setIgnoreRepaint(true);

        ((Window) window).add(canvas);

        window.setVisible(true);

        if(!canvas.isDisplayable()) {
            canvas.addNotify();
        }

        Filament.INSTANCE.init();
        engine = Engine.Companion.create(RenderEngine.FILAMENT_VULKAN.equals(scene.getRenderEngine())
                ? Engine.Backend.VULKAN
                : Engine.Backend.OPENGL);

        MaterialBuilder.Companion.init();

        renderer = engine.createRenderer();
        swapChain = engine.createSwapChain(window.getBaseWidth(), window.getBaseHeight(), 0L);

        filamentScene = engine.createScene();

        view = engine.createView();

        io.github.erkko68.filament.Camera cam = engine.createCamera(engine.getEntityManager().create());

        cam.setProjection(60, (double) window.getBaseWidth() / window.getBaseHeight(), 0.1, 1000, io.github.erkko68.filament.Camera.Fov.VERTICAL);

        view.setCamera(cam);
        view.setScene(filamentScene);
        view.setShadowingEnabled(true);
        view.setViewport(new Viewport(0, 0, window.getBaseWidth(), window.getBaseHeight()));

        filamentScene.setSkybox(
                new Skybox.Builder()
                        .color(0.192156863f, 0.337254902f, 0.474509804f, 1.0f)
                        .build(engine)
        );

        int sun = engine.getEntityManager().create();
        new LightManager.Builder(LightManager.Type.SUN)
                .color(1.0f, 0.95f, 0.8f)
                .intensity(1000000)
                .direction(0.5f, -1.0f, 0.3f)
                .shadowOptions(getShadowOptions())
                .castShadows(true)
                .build(engine, sun);

        filamentScene.addEntity(sun);

        for(ObjectComponent component : getParentScene().getSceneEventHandler().getComponents("onFilamentInitialization")) {
            component.onFilamentInitialization();
        }
    }
}