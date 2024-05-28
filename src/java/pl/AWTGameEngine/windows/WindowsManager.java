package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
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

    private final List<Window> windows = new ArrayList<>();
    private Window defaultWindow;
    private Font defaultFont;

    public Window createWindow(String scenePath) {
        AppProperties appProperties = Dependencies.getAppProperties();
        Window window = new Window(Window.RenderEngine.valueOf(appProperties.getProperty("renderEngine").toUpperCase()));
        if(windows.size() == 0) {
            defaultFont = window.getFont();
        }
        window.setSceneLoader(new SceneLoader(window));
        window.setProjectManager(new ProjectManager(window));
        window.setResizable(false);
        window.setTitle(appProperties.getProperty("title"));

        Sprite icon = Dependencies.getResourceManager().getResourceAsSprite(appProperties.getProperty("icon"));
        if(icon != null) {
            window.setIconImage(icon.getImage());
        }

        window.setWindowListener(new WindowListener(window));
        window.addComponentListener(window.getWindowListener());

        GameLoop loop = new GameLoop(window, true);
        loop.setFPS(appProperties.getPropertyAsInteger("renderFps"));
        window.setRenderLoop(loop);
        loop.start();

        loop = new GameLoop(window, false);
        loop.setFPS(appProperties.getPropertyAsInteger("updateFps"));
        window.setUpdateLoop(loop);
        window.getSceneLoader().loadSceneFile(scenePath);
        loop.start();

        windows.add(window);
        window.setVisible(true);
        return window;
    }

    public void createDefaultWindow() {
        defaultWindow = createWindow(Dependencies.getAppProperties().getProperty("main"));
        defaultWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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