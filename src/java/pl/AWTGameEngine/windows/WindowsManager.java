package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WindowsManager {

    private static final List<Window> windows = new ArrayList<>();
    private static Window defaultWindow;

    public static Window createWindow(String scenePath) {
        Window window = new Window();
        window.setSceneLoader(new SceneLoader(window));
        window.setResizable(false);
        Properties customProperties = AppProperties.getCustomProperties(window.getSceneLoader().getScenePropertiesPath(scenePath));
        if(Boolean.parseBoolean(AppProperties.getProperty("fullscreen"))) {
            window.setFullScreen(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setUndecorated(true);
        }
        window.setPanel(new GamePanel(window));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setKeyListener(new KeyListener());
        window.setMouseListener(new MouseListener(window));
        window.getSceneLoader().loadSceneFile(scenePath);
        windows.add(window);
        GameLoop loop = new GameLoop(window);
        int fps;
        if(customProperties != null) {
            fps = AppProperties.getPropertyAsInteger("fps", customProperties);
        } else {
            fps = AppProperties.getPropertyAsInteger("fps");
        }
        if(fps < 1) {
            fps = 1;
        }
        loop.setFPS(fps);
        loop.start();
        window.setLoop(loop);
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

}