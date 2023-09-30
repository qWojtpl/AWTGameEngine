package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.List;

public class SceneLoader {

    private final Window window;

    public SceneLoader(Window window) {
        this.window = window;
    }

    public void loadScene(String sceneName) {
        window.getPanel().removeAll();
        window.setCurrentScene(new Scene(sceneName, window));
        window.getCurrentScene().setCamera(new Camera());
        window.getCurrentScene().getCamera().setZoom(window.getPanel().getMultipler() / 2f);
        List<String> lines;
        File sceneFile = ResourceManager.getResource(sceneName + ".scene");
        if(sceneFile == null) {
            System.out.println("Scene not found.");
            return;
        }
        try {
            lines = Files.readAllLines(sceneFile.toPath(), StandardCharsets.UTF_8);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        for(String line : lines) {
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
    }

}
