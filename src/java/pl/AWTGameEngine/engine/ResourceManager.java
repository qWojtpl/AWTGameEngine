package pl.AWTGameEngine.engine;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class ResourceManager {

    private final static HashMap<String, List<String>> resources = new HashMap<>();
    private final static HashMap<String, Image> imageResources = new HashMap<>();

    public static List<String> getResource(String name) {
        if(resources.containsKey(name)) {
            return resources.get(name);
        }
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception();
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

    public static Image getResourceAsImage(String name) {
        if(imageResources.containsKey(name)) {
            return imageResources.get(name);
        }
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception();
            }
            Image img = ImageIO.read(stream);
            imageResources.put(name, img);
            stream.close();
            return img;
        } catch(Exception e) {
            System.out.println("Cannot get image from resource: " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        try {
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if(stream == null) {
                throw new Exception();
            }
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

    public static HashMap<String, Image> getImageResources() {
        return new HashMap<>(imageResources);
    }

}
