package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
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
        Path path = null;
        URL url = Main.class.getClassLoader().getResource(sceneName + ".scene");
        if(url == null) {
            System.out.println("Scene not found.");
            return;
        }
        URI uri = null;
        try {
            uri = url.toURI();
            String[] array = url.toURI().toString().split("!");
            FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), new HashMap<>());
            path = fs.getPath(array[1]);

        } catch(URISyntaxException | IOException e) {
            e.printStackTrace();
            return;
        } catch(IllegalArgumentException e) {
            if(uri != null) {
                path = Paths.get(uri);
            }
        }
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch(IOException e) {
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
            GameObject object = window.getCurrentScene().createGameObject(objectName);
            object.deserialize(data.get(objectName));
        }
    }

}
