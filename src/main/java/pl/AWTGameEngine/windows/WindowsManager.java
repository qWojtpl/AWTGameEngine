package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.CommandConsole;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.loops.*;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Command("wmanager")
public class WindowsManager extends CommandConsole.ParentCommand {

    private final List<Window> windows = new ArrayList<>();
    private Window defaultWindow;
    private Font defaultFont;

    private SplashScreenWindow splashScreenWindow;

    @Command(value = "create", argumentNames = { "path" })
    public Window createWindow(String scenePath) {
        return createWindow(scenePath, null);
    }

    public Window createWindow(String scenePath, RenderEngine renderEngine) {
        AppProperties appProperties = Dependencies.getAppProperties();
        boolean server = appProperties.getPropertyAsBoolean("server");
        if (renderEngine == null) {
            renderEngine = RenderEngine.valueOf(appProperties.getProperty("renderEngine").toUpperCase());
        }
        Window window = new Window(server);
        if (windows.isEmpty()) {
            defaultFont = window.getFont();
        }
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    public Window getDefaultWindow() {
        return defaultWindow;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public List<Window> getWindows() {
        return windows;
    }

    public void removeWindow(Window window) {
        windows.remove(window);
    }

    public SplashScreenWindow getSplashScreenWindow() {
        return this.splashScreenWindow;
    }

    private void createLoops(Window window) {
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

    private void startLoops(Window window, boolean server) {
        window.getUpdateLoop().start();

        if(!server) {
            window.getRenderLoop().start();
        }

        window.getPhysicsLoop().start();
        window.getNetLoop().start();
    }

    private void showSplashScreen() {
        splashScreenWindow = new SplashScreenWindow();
        splashScreenWindow.init();
    }

    public void showWindow(Window window) {
        if(!window.isServerWindow()) {
            window.setVisible(true);
        }
        if(splashScreenWindow != null) {
            splashScreenWindow.close();
            splashScreenWindow = null;
        }
    }

}