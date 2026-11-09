package pl.AWTGameEngine.engine.panels;

import io.github.erkko68.filament.*;
import io.github.erkko68.filament.filamat.MaterialBuilder;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFilament;
import pl.AWTGameEngine.engine.helpers.ImageHelper;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.transform.Vector3;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;
import pl.AWTGameEngine.windows.HeadlessWindow;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class FilamentPanel extends Panel3D implements PanelObject {

    // AWT + engine
    private final Scene scene;
    private final BaseWindow window;
    private final Camera camera;
    private final PhysXManager physXManager;
    private Canvas canvas;
    private boolean initialized = false;

    // Filament
    private Engine engine;
    private io.github.erkko68.filament.Scene filamentScene;
    private SwapChain swapChain;
    private Renderer renderer;
    private View view;
    private LightManager.ShadowOptions shadowOptions;

    // Frame pool
    private final int POOL_SIZE = 3;
    private final List<RenderFrame> renderFrames = new ArrayList<>();
    private int currentFrame = 0;

    private int cachedWidth = 0;
    private int cachedHeight = 0;

    private BufferStrategy strategy;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private BufferedImage guiImage;

    class RenderFrame {

        private final Texture.PixelBufferDescriptor pixelBufferDescriptor;
        private final BufferedImage bufferedImage;
        private final byte[] pixels;

        private volatile boolean isDisplaying = false;

        public RenderFrame() {
            this.bufferedImage = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            this.pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

            int sizeInBytes = window.getWidth() * window.getHeight() * 3;

            this.pixelBufferDescriptor = new Texture.PixelBufferDescriptor(
                    pixels,
                    sizeInBytes,
                    Texture.Format.RGB,
                    Texture.Type.UBYTE,
                    1, 0, 0, 0,
                    () -> {
                        while(renderFrames.get(renderFrames.indexOf(this) == 0 ? renderFrames.size() - 1 : renderFrames.indexOf(this) - 1).isDisplaying) {}
                        isDisplaying = true;
                        service.submit(() -> {
                            present(this);
                        });
                        return null;
                    }
            );
        }

        public void read() {
            isDisplaying = false;
            renderer.readPixels(0, 0, window.getWidth(), window.getHeight(), pixelBufferDescriptor);
        }

        public BufferedImage getBufferedImage() {
            return this.bufferedImage;
        }

    }

    public FilamentPanel(Scene scene) {
        this.scene = scene;
        this.window = scene.getWindow();
        this.camera = new Camera(this);
        this.physXManager = PhysXManager.getInstance();
        physXManager.createPhysXScene(scene);
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

    public PhysXManager getPhysXManager() {
        return this.physXManager;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public View getView() {
        return this.view;
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
            for(Vector3 locked : locks) {
                try {
                    locked.unlock();
                } catch(IllegalMonitorStateException e) {
                    Logger.exception("Failed to unlock Vector3", e);
                }
            }

            if(currentFrame == renderFrames.size()) {
                currentFrame = 0;
            }

            while(renderFrames.get(currentFrame).isDisplaying) {}

            if(renderer.beginFrame(swapChain, System.nanoTime())) {
                renderer.render(view);
                renderFrames.get(currentFrame).read();
                renderer.endFrame();
                currentFrame++;
            }
        } catch(Exception e) {
            Logger.exception("Unhandled exception caught while running an iteration of Filament render request", e);
        }
    }

    private void present(RenderFrame renderFrame) {
        if(canvas == null || !canvas.isDisplayable()) {
            return;
        }
        ImageHelper.swapRGBandBGRandBlend(renderFrame.pixels, ((DataBufferInt) guiImage.getRaster().getDataBuffer()).getData());
        try {
            do {
                do {
                    Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
                    try {
                        g.drawImage(renderFrame.getBufferedImage(), 0, 0, null);
                    } finally {
                        g.dispose();
                    }
                } while(strategy.contentsRestored());
                strategy.show();
            } while(strategy.contentsLost());
        } finally {
            renderFrame.isDisplaying = false;
        }
    }

    private void ensureBuffersSize(int width, int height) {
        if(width == cachedWidth && height == cachedHeight) {
            return;
        }

        this.cachedWidth = width;
        this.cachedHeight = height;
        this.currentFrame = 0;

        for(int i = 0; i < POOL_SIZE; i++) {
            if(renderFrames.size() == i) {
                renderFrames.add(new RenderFrame());
            } else {
                renderFrames.set(i, new RenderFrame());
            }
        }

    }

    @Override
    public void unload() {
        PhysXManager.getInstance().removePhysXScene(scene);
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

    public void setGUI(BufferedImage image) {
        this.guiImage = image;
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

        if(strategy == null) {
            canvas.createBufferStrategy(2);
            strategy = canvas.getBufferStrategy();
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            try {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            } finally {
                g.dispose();
            }
            strategy.show();
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