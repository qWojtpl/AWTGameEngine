package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.engine.loops.PhysicsLoop;
import pl.AWTGameEngine.engine.loops.RenderLoop;
import pl.AWTGameEngine.engine.loops.UpdateLoop;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WindowsManager {

    private final List<Window> windows = new ArrayList<>();
    private Window defaultWindow;
    private Font defaultFont;

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
        if (icon != null) {
            window.setIconImage(icon.getImage());
        }

        BaseLoop updateLoop = new UpdateLoop(window);
        updateLoop.setTargetFps(appProperties.getPropertyAsInteger("updateFps"));
        window.setUpdateLoop(updateLoop);

        BaseLoop renderLoop = new RenderLoop(window);
        renderLoop.setTargetFps(appProperties.getPropertyAsInteger("renderFps"));
        window.setRenderLoop(renderLoop);

        BaseLoop physicsLoop = new PhysicsLoop(window);
        physicsLoop.setTargetFps(appProperties.getPropertyAsInteger("physicsFps"));
        window.setPhysicsLoop(physicsLoop);

        window.init();
        window.getSceneLoader().loadSceneFile(scenePath, renderEngine);
        windows.add(window);

        updateLoop.start();

        if (!server) {
            renderLoop.start();
        }

        physicsLoop.start();

        return window;
    }

    public void createDefaultWindow() {
        defaultWindow = createWindow(Dependencies.getAppProperties().getProperty("main"));
    }

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
}