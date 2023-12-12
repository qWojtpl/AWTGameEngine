package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class WindowsManager {

    private static final List<Window> windows = new ArrayList<>();
    private static Window defaultWindow;

    public static Window createWindow(String scenePath) {
        Properties customProperties = AppProperties.getCustomProperties(SceneLoader.getScenePropertiesPath(scenePath));
        if(customProperties != null) {
            if(Boolean.parseBoolean(AppProperties.getProperty("fullscreen", customProperties))) {
                return createWindow(scenePath, true);
            } else {
                return createWindow(scenePath, false);
            }
        }
        return createWindow(scenePath, Boolean.parseBoolean(AppProperties.getProperty("fullscreen")));
    }

    public static Window createWindow(String scenePath, boolean fullScreen) {
        Window window = new Window();
        window.setSceneLoader(new SceneLoader(window));
        window.setResizable(false);
        window.setTitle(AppProperties.getProperty("title"));
        if(fullScreen) {
            window.setFullScreen(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setUndecorated(true);
        }
        window.setWindowListener(new WindowListener(window));
        GameLoop loop = new GameLoop(window);
        loop.setFPS(AppProperties.getPropertyAsInteger("fps"));
        window.setLoop(loop);
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

    public static List<Window> getWindows() {
        return windows;
    }

    public static void removeWindow(Window window) {
        windows.remove(window);
    }

}