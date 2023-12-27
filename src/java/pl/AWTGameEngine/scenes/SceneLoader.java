package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

public class SceneLoader {

    private final Window window;

    public SceneLoader(Window window) {
        this.window = window;
    }

    public void loadSceneFile(String scenePath) {
        Logger.log(2, "Loading scene: " + scenePath);
        if(window.getCurrentScene() != null) {
            Scene scene = window.getCurrentScene();
            window.setCurrentScene(null);
            scene.removeAllObjects();
        }
        Properties customProperties = AppProperties.getCustomProperties(getScenePropertiesPath(scenePath));
        if(customProperties != null) {
            window.setTitle(AppProperties.getProperty("title", customProperties));
            window.getRenderLoop().setFPS(AppProperties.getPropertyAsInteger("renderFps", customProperties));
            window.getUpdateLoop().setFPS(AppProperties.getPropertyAsInteger("updateFps", customProperties));
            if(AppProperties.getProperty("multiplier", customProperties) != null) {
                window.setMultiplier(AppProperties.getPropertyAsDouble("multiplier", customProperties));
            } else {
                window.setMultiplier(AppProperties.getPropertyAsDouble("multiplier"));
            }
        } else {
            window.setTitle(AppProperties.getProperty("title"));
            window.setMultiplier(AppProperties.getPropertyAsDouble("multiplier"));
        }
        window.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        window.getPanel().removeAll();
        window.setCurrentScene(new Scene(scenePath, window));
        window.getCurrentScene().getPanelRegistry().addPanel(window.getPanel());
        window.setLocationRelativeTo(null);
        List<String> sceneLines = ResourceManager.getResource(scenePath);
        if(sceneLines == null) {
            Logger.log(1, "Scene " + scenePath + " not found.");
            return;
        }
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        for(String line : sceneLines) {
            boolean stringOpened = false;
            boolean valueOpened = false;
            String key = "";
            String value = "";
            for(int i = 0; i < line.length(); i++) {
                if(line.charAt(i) == '"') {
                    stringOpened = !stringOpened;
                    continue;
                } else if(line.charAt(i) == ';' && !stringOpened) {
                    break;
                } else if(line.charAt(i) == '=' && !valueOpened) {
                    valueOpened = true;
                    continue;
                }
                if(!valueOpened) {
                    key += line.charAt(i);
                } else {
                    value += line.charAt(i);
                }
            }
            data.put(key, value);
        }
        for(String objectName : data.keySet()) {
            GameObject object = window.getCurrentScene().createGameObject(objectName);
            object.deserialize(data.get(objectName));
        }
        window.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Logger.log(2, "Scene loaded.");
    }

    public static String getScenePropertiesPath(String scenePath) {
        String path = "";
        for(int i = 0; i < scenePath.length() - 5; i++) {
            path += scenePath.charAt(i);
        }
        path += "properties";
        return path;
    }

}