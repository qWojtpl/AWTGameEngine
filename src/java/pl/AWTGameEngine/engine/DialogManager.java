package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;
import pl.AWTGameEngine.components.TextArea;

public class DialogManager {

    DialogManager() {

    }

    public static void createError(String message) {
        Window w = WindowsManager.createWindow(AppProperties.getProperty("error"));
        GameObject go = w.getCurrentScene().getGameObjectByName("@textArea");
        ((TextArea) go.getComponentsByClass(TextArea.class).get(0)).setText(message);
    }

}
