package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.CommandConsole;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.engine.loops.*;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.scenes.SceneLoader;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Command("wmanager")
public class WindowsManager extends CommandConsole.ParentCommand {

    private final List<BaseWindow> windows = new ArrayList<>();
    private BaseWindow defaultWindow;
    private Font defaultFont;

    private SplashScreenWindow splashScreenWindow;

    @Command(value = "create", argumentNames = { "path" })
    public BaseWindow createWindow(String scenePath) {
        return createWindow(scenePath, null, true);
    }

    public BaseWindow createWindow(String scenePath, RenderEngine renderEngine, boolean decorated) {
        AppProperties appProperties = Dependencies.getAppProperties();
        boolean server = appProperties.getPropertyAsBoolean("server");
        if(renderEngine == null) {
            renderEngine = RenderEngine.valueOf(appProperties.getProperty("renderEngine").toUpperCase());
        }

        BaseWindow window;

        if(server) {
            window = new HeadlessWindow();
        } else {
            window = new Window(server);
            if(!decorated) {
                ((Window) window).setUndecorated(true);
                ((Window) window).setType(java.awt.Window.Type.UTILITY);
            }
        }

        if (windows.isEmpty()) {
            defaultFont = window.getFont();
        }
        
        window.setSceneLoader(new SceneLoader(window));

        window.setTitle(appProperties.getProperty("title"));

        Sprite icon = Dependencies.getResourceManager().getResourceAsSprite(appProperties.getProperty("icon"));
        if(icon != null) {
            window.setIconImage(icon.getImage());
        }

        PhysXManager.getInstance();

        createLoops(window);

        window.init();
        window.getSceneLoader().loadSceneFile(scenePath, renderEngine, false);
        windows.add(window);

        startLoops(window, server);

        showWindow(window);

        return window;
    }

    @Command("createDefault")
    public void createDefaultWindow() {
        showSplashScreen();
        defaultWindow = createWindow(Dependencies.getAppProperties().getProperty("main"));
    }

    @Command("default")
    public BaseWindow getDefaultWindow() {
        return defaultWindow;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public List<BaseWindow> getWindows() {
        return windows;
    }

    public void removeWindow(BaseWindow window) {
        windows.remove(window);
    }

    public SplashScreenWindow getSplashScreenWindow() {
        return this.splashScreenWindow;
    }

    private void createLoops(BaseWindow window) {
        AppProperties appProperties = Dependencies.getAppProperties();
        BaseLoop updateLoop = new UpdateLoop(window);
        updateLoop.setTargetFps(appProperties.getPropertyAsInteger("updateFps"));
        window.setUpdateLoop(updateLoop);

        BaseLoop renderLoop = new RenderLoop(window);
        renderLoop.setTargetFps(appProperties.getPropertyAsInteger("renderFps"));
        window.setRenderLoop(renderLoop);

        BaseLoop physicsLoop = new PhysicsLoop(window);
        physicsLoop.setTargetFps(appProperties.getPropertyAsInteger("physicsFps"));
        window.setPhysicsLoop(physicsLoop);

        BaseLoop netLoop = new NetLoop(window);
        netLoop.setTargetFps(appProperties.getPropertyAsInteger("updateFps"));
        window.setNetLoop(netLoop);
    }

    private void startLoops(BaseWindow window, boolean server) {
        window.getUpdateLoop().start();

        if(!server) {
            window.getRenderLoop().start();
        }

        window.getPhysicsLoop().start();
        window.getNetLoop().start();
    }

    private void showSplashScreen() {
        if(!Dependencies.getAppProperties().getPropertyAsBoolean("splashScreen")) {
            return;
        }
        Logger.info("Creating splash screen...");
        splashScreenWindow = new SplashScreenWindow();
        splashScreenWindow.init();
    }

    public void showWindow(BaseWindow window) {
        window.updateDialogs();
        window.setVisible(true);
        if(splashScreenWindow != null) {
            splashScreenWindow.close();
            splashScreenWindow = null;
        }
    }

    public void close(BaseWindow window) {
        removeWindow(window);
        if(window.equals(defaultWindow)) {
            if(window.getCurrentScene() != null) {
                for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onWindowClosing")) {
                    component.onWindowClosing();
                }
            }
            // kill physics loop, because remove actor operation
            // can't be executed while simulation is running,
            // so we need to wait for PhysicsLoop to end a simulation.
            window.getPhysicsLoop().kill(() -> {
                window.unloadScenes();
                PhysXManager.getInstance().cleanup();
                window.getNetLoop().kill();
                window.getRenderLoop().kill();
                window.getUpdateLoop().kill();
                Logger.info("Stopped app.");
                System.exit(0);
            });
        }
    }

}