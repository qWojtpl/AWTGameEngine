package pl.AWTGameEngine.engine.panels;

import com.jogamp.opengl.*;
import io.github.erkko68.filament.*;
import io.github.erkko68.filament.filamat.MaterialBuilder;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFilament;
import pl.AWTGameEngine.engine.helpers.FilamentHelper;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.transform.Vector3;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;
import pl.AWTGameEngine.windows.HeadlessWindow;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
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

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager3D == null) {
            return;
        }
        if(!initialized) {
            initFilament();
            initialized = true;
        }
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
                renderer.endFrame();
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

    @Override
    public void unload() {
        PhysXManager.getInstance().removeScene(scene);
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

    private void initFilament() {
        this.canvas = new Canvas();
        canvas.setFocusable(false);
        ((Window) window).add(canvas);

        window.setVisible(true);

        if (!canvas.isDisplayable()) {
            canvas.addNotify();
        }

        Filament.INSTANCE.init();
        engine = Engine.Companion.create(Engine.Backend.VULKAN);

        MaterialBuilder.Companion.init();

        renderer = engine.createRenderer();
        swapChain = engine.createSwapChain(new NativeSurface(FilamentHelper.getHWND(canvas)));

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

        var options = new LightManager.ShadowOptions();
        options.setMapSize(8192);
        options.setShadowCascades(10);
        options.setBlurWidth(0);

        int sun = engine.getEntityManager().create();
        new LightManager.Builder(LightManager.Type.SUN)
                .color(1.0f, 0.95f, 0.8f)
                .intensity(1000000)
                .direction(0.5f, -1.0f, 0.3f)
                .shadowOptions(options)
                .castShadows(true)
                .build(engine, sun);

        filamentScene.addEntity(sun);

        int light = engine.getEntityManager().create();
        new LightManager.Builder(LightManager.Type.POINT)
                .color(1, 0, 0)
                .intensity(1000000000)
                .position(0, 0, 0)
                .falloff(100.0f)
                .castShadows(true)
                .shadowOptions(options)
                .build(engine, light);

        filamentScene.addEntity(light);
    }

}