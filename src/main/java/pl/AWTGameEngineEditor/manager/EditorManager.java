package pl.AWTGameEngineEditor.manager;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.windows.Window;

public class EditorManager {

    private static EditorManager instance;
    private Window gameViewWindow;

    EditorManager() {}

    public Window getGameViewWindow() {
        return this.gameViewWindow;
    }

    public void setGameViewWindow(Window window) {
        this.gameViewWindow = window;
    }

    public Window createWindow(String scenePath, RenderEngine renderEngine) {
        Window window = (Window) Dependencies.getWindowsManager().createWindow(scenePath, renderEngine, true);
        window.setResizable(false);
        window.setSize(800, 600);
        window.setRatio(4, 3);
        window.toFront();
        return window;
    }

    public static EditorManager getInstance() {
        if(instance == null) {
            instance = new EditorManager();
        }
        return instance;
    }





}
