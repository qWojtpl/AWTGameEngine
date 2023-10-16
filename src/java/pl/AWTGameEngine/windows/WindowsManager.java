package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WindowsManager {

    private static final List<Window> windows = new ArrayList<>();
    private static Window defaultWindow;

    public static Window createWindow(String sceneName, boolean fullScreen) {
        Window window = new Window();
        window.setResizable(false);
        if(fullScreen) {
            window.setFullScreen(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setUndecorated(true);
        }
        window.setTitle("AWTGameEngine");
        window.setPanel(new GamePanel(window));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setKeyListener(new KeyListener());
        window.setMouseListener(new MouseListener(window));
        window.setSceneLoader(new SceneLoader(window));
        window.getSceneLoader().loadSceneFile(sceneName);
        windows.add(window);
        GameLoop loop = new GameLoop(window);
        loop.start();
        window.setLoop(loop);
        window.setVisible(true);
        return window;
    }

    public static void createDefaultWindow() {
        defaultWindow = createWindow("main", false);
        defaultWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static Window getDefaultWindow() {
        return defaultWindow;
    }

    public static List<Window> getWindows() {
        return windows;
    }

}