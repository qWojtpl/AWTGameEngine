package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
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

    public Window createWindow(String scenePath, Window.RenderEngine renderEngine) {
        AppProperties appProperties = Dependencies.getAppProperties();
        boolean server = appProperties.getPropertyAsBoolean("server");
        if (renderEngine == null) {
            renderEngine = Window.RenderEngine.valueOf(appProperties.getProperty("renderEngine").toUpperCase());
        }
        Window window = new Window(
                renderEngine,
                server);
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
        updateLoop.setFPS(appProperties.getPropertyAsInteger("updateFps"));
        window.setUpdateLoop(updateLoop);

        BaseLoop renderLoop = new RenderLoop(window);
        renderLoop.setFPS(appProperties.getPropertyAsInteger("renderFps"));
        window.setRenderLoop(renderLoop);

        BaseLoop physicsLoop = new PhysicsLoop(window);
        physicsLoop.setFPS(appProperties.getPropertyAsInteger("physicsFps"));
        window.setPhysicsLoop(physicsLoop);

        window.init();
        window.getSceneLoader().loadSceneFile(scenePath);
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