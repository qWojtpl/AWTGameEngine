package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.ProjectManager;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WindowsManager {

    private static final List<Window> windows = new ArrayList<>();
    private static Window defaultWindow;
    private static Font defaultFont;

    WindowsManager() {
        
    }

    public static Window createWindow(String scenePath) {
        Window window = new Window(Window.RenderEngine.valueOf(AppProperties.getProperty("renderEngine").toUpperCase()));
        if(windows.size() == 0) {
            defaultFont = window.getFont();
        }
        window.setSceneLoader(new SceneLoader(window));
        window.setProjectManager(new ProjectManager(window));
        window.setResizable(false);
        window.setTitle(AppProperties.getProperty("title"));

        Sprite icon = ResourceManager.getResourceAsSprite(AppProperties.getProperty("icon"));
        if(icon != null) {
            window.setIconImage(icon.getImage());
        }

        window.setWindowListener(new WindowListener(window));
        window.addComponentListener(window.getWindowListener());

        GameLoop loop = new GameLoop(window, true);
        loop.setFPS(AppProperties.getPropertyAsInteger("renderFps"));
        window.setRenderLoop(loop);
        loop.start();

        loop = new GameLoop(window, false);
        loop.setFPS(AppProperties.getPropertyAsInteger("updateFps"));
        window.setUpdateLoop(loop);
        window.getSceneLoader().loadSceneFile(scenePath);
        loop.start();

        windows.add(window);
        window.setVisible(true);
        return window;
    }

    public static void createDefaultWindow() {
        defaultWindow = createWindow(AppProperties.getProperty("main"));
        defaultWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static Window getDefaultWindow() {
        return defaultWindow;
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static List<Window> getWindows() {
        return windows;
    }

    public static void removeWindow(Window window) {
        windows.remove(window);
    }

}