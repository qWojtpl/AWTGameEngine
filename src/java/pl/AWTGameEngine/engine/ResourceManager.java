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
            System.out.println("Not found resource: " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static Sprite getResourceAsSprite(String name) {
        if(spriteResources.containsKey(name)) {
            return spriteResources.get(name);
        }
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception();
            }
            Image img = ImageIO.read(stream);
            Sprite sprite = new Sprite(name, img);
            spriteResources.put(name, sprite);
            stream.close();
            return sprite;
        } catch(Exception e) {
            System.out.println("Cannot get image from resource: " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        if(streamResources.containsKey(name)) {
            return streamResources.get(name);
        }
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception();
            }
            streamResources.put(name, stream);
            return stream;
        } catch(Exception e) {
            System.out.println("Cannot get stream from resource: " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, List<String>> getResources() {
        return new HashMap<>(resources);
    }

    public static HashMap<String, Sprite> getSpriteResources() {
        return new HashMap<>(spriteResources);
    }

}
