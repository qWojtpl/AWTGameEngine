package pl.AWTGameEngineEditor.manager;

import pl.AWTGameEngine.windows.Window;

public class EditorManager {

    private static EditorManager instance;
    private Window gameViewWindow;

    EditorManager() {

    }

    public Window getGameViewWindow() {
        return this.gameViewWindow;
    }

    public void setGameViewWindow(Window window) {
        this.gameViewWindow = window;
    }

    public static EditorManager getInstance() {
        if(instance == null) {
            instance = new EditorManager();
        }
        return instance;
    }





}
