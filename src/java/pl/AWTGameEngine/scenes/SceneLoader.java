package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.GameObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SceneLoader {

    private static Scene currentScene;

    public static void loadScene(String sceneName) {
        currentScene = new Scene(sceneName);
        List<String> lines;
        try {
            URL url = Main.class.getClassLoader().getResource(sceneName + ".scene");
            if(url == null) {
                System.out.println("Scene not found.");
                return;
            }
            Path path = Paths.get(url.toURI());
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch(URISyntaxException | IOException e) {
            e.printStackTrace();
            return;
        }
        HashMap<String, String> data = new HashMap<>();
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
            GameObject object;
            try {
                object = currentScene.createGameObject(objectName);
            } catch(DuplicatedObjectException e) {
                System.out.println(e.getMessage());
                continue;
            }
            object.deserialize(data.get(objectName));
        }
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

}
