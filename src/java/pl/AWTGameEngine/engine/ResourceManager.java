package pl.AWTGameEngine.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class ResourceManager {

    private final static HashMap<String, File> resources = new HashMap<>();
    private final static HashMap<String, Image> imageResources = new HashMap<>();

    public static File getResource(String name) {
        if(resources.containsKey(name)) {
            return resources.get(name);
        }
        try {
            URL url = ResourceManager.class.getClassLoader().getResource(name);
            if(url == null) {
                throw new Exception();
            }
            String path = url.getPath();
            File file = new File(path);
            resources.put(name, file);
            return file;
        } catch(Exception e) {
            System.out.println("Not found resource: " + name);
        }
        return null;
    }

    public static Image getResourceAsImage(String name) {
        if(imageResources.containsKey(name)) {
            return imageResources.get(name);
        }
        File file = getResource(name);
        if(file == null) {
            return null;
        }
        try {
            Image img = ImageIO.read(file);
            imageResources.put(name, img);
            return img;
        } catch(IOException e) {
            System.out.println("Cannot get image from resource: " + name);
        }
        return null;
    }

    public static HashMap<String, File> getResources() {
        return new HashMap<>(resources);
    }

    public static HashMap<String, Image> getImageResources() {
        return new HashMap<>(imageResources);
    }

}
