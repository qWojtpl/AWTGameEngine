package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WindowsManager {

    private static final List<Window> windows = new ArrayList<>();
    private static Window defaultWindow;

    public static Window createWindow(String sceneName) {
        Window window = new Window();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
/*        if(isFullScreen()) {
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setUndecorated(true);
        }*/
        window.setTitle("AWTGameEngine");
        window.setPanel(new GamePanel(window));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setKeyListener(new KeyListener());
        SceneLoader.loadScene(sceneName, window);
        windows.add(window);
        GameLoop loop = new GameLoop(window);
        loop.start();
        window.setLoop(loop);
        return window;
    }

    public static void createDefaultWindow() {
        defaultWindow = createWindow("main");
    }

    public static Window getDefaultWindow() {
        return defaultWindow;
    }

    public static List<Window> getWindows() {
        return windows;
    }

}
