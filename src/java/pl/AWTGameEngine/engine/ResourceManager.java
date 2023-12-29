package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.Sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ResourceManager {

    private final static HashMap<String, List<String>> resources = new HashMap<>();
    private final static HashMap<String, Sprite> spriteResources = new HashMap<>();
    private final static HashMap<String, InputStream> streamResources = new HashMap<>();

    public static List<String> getResource(String name) {
        if(resources.containsKey(name)) {
            return resources.get(name);
        }
        Logger.log(2, "Reading file resource: " + name);
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception("Stream is null.");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            List<String> lines = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null) {
                lines.add(line);
            }
            resources.put(name, lines);
            stream.close();
            return lines;
        } catch(Exception e) {
            Logger.log("Cannot get file resource: " + name, e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public static Sprite getResourceAsSprite(String name) {
        if(spriteResources.containsKey(name)) {
            return spriteResources.get(name);
        }
        Logger.log(2, "Reading sprite resource: " + name);
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception("Stream is null.");
            }
            Image img = ImageIO.read(stream);
            Sprite sprite = new Sprite(name, img);
            spriteResources.put(name, sprite);
            stream.close();
            return sprite;
        } catch(Exception e) {
            Logger.log("Cannot get sprite from resource: " + name, e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        if(streamResources.containsKey(name)) {
            return streamResources.get(name);
        }
        Logger.log(2, "Reading stream resource: " + name);
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception("Stream is null.");
            }
            streamResources.put(name, stream);
            return stream;
        } catch(Exception e) {
            Logger.log("Cannot get stream from resource: " + name, e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public static HashMap<String, List<String>> getResources() {
        return new HashMap<>(resources);
    }

    public static HashMap<String, Sprite> getSpriteResources() {
        return new HashMap<>(spriteResources);
    }

    public static HashMap<String, InputStream> getStreamResources() {
        return new HashMap<>(streamResources);
    }

}
