package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.listeners.WindowListener;
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
        AppProperties appProperties = Dependencies.getAppProperties();
        Window window = new Window(Window.RenderEngine.valueOf(appProperties.getProperty("renderEngine").toUpperCase()));
        if(windows.isEmpty()) {
            defaultFont = window.getFont();
        }
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSceneLoader(new SceneLoader(window));
        //window.setResizable(false);
        window.setTitle(appProperties.getProperty("title"));

        Sprite icon = Dependencies.getResourceManager().getResourceAsSprite(appProperties.getProperty("icon"));
        if(icon != null) {
            window.setIconImage(icon.getImage());
        }

        GameLoop updateLoop = new GameLoop(window, false);
        updateLoop.setFPS(appProperties.getPropertyAsInteger("updateFps"));
        window.setUpdateLoop(updateLoop);

        GameLoop renderLoop = new GameLoop(window, true);
        renderLoop.setFPS(appProperties.getPropertyAsInteger("renderFps"));
        window.setRenderLoop(renderLoop);

        window.init();
        window.getSceneLoader().loadSceneFile(scenePath);
        windows.add(window);

        updateLoop.start();
        renderLoop.start();

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