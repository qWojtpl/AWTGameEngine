package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.AudioClip;
import pl.AWTGameEngine.objects.Sprite;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceManager {

    private final static HashMap<String, List<String>> resources = new HashMap<>();
    private final static HashMap<String, Sprite> spriteResources = new HashMap<>();
    private final static HashMap<String, InputStream> streamResources = new HashMap<>();
    private final static HashMap<String, URL> urlResources = new HashMap<>();
    private final static List<AudioClip> audioClips = new ArrayList<>();

    ResourceManager() {

    }

    public static void copyResource(String name, String path) {
        Logger.log(2, "Copying resource: " + name + " to " + path);
        name = getResourceName(name);
        try {
            InputStream stream = getStream(name);
            if(stream == null) {
                throw new Exception("Stream is null. Cannot find this resource.");
            }
            File newFile = new File(path);
            String[] dirSplit = path.split("/");
            String currentDir = "";
            for(int i = 0; i < dirSplit.length - 1; i++) {
                currentDir += dirSplit[i] + "/";
                File directory = new File(currentDir);
                directory.mkdir();
            }
            if(!newFile.exists()) {
                newFile.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(newFile, false);
            int read;
            byte[] bytes = new byte[8192];
            while((read = stream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }
            stream.close();
            fileOutputStream.close();
        } catch(Exception e) {
            Logger.log("Cannot copy file resource: " + name, e);
        }
    }

    public static List<String> getResource(String name) {
        name = getResourceName(name);
        if(resources.containsKey(name)) {
            return resources.get(name);
        }
        Logger.log(2, "Reading file resource: " + name);
        try {
            InputStream stream = getStream(name);
            if(stream == null) {
                throw new Exception("Stream is null. Cannot find this resource.");
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
            Logger.log("Cannot get file resource: " + name, e);
        }
        return null;
    }

    public static Sprite getResourceAsSprite(String name) {
        name = getResourceName(name);
        if(spriteResources.containsKey(name)) {
            return spriteResources.get(name);
        }
        Logger.log(2, "Reading sprite resource: " + name);
        try {
            InputStream stream = getStream(name);
            if(stream == null) {
                throw new Exception("Stream is null. Cannot find this resource.");
            }
            Image img = ImageIO.read(stream);
            Sprite sprite = new Sprite(name, img);
            spriteResources.put(name, sprite);
            stream.close();
            return sprite;
        } catch(Exception e) {
            Logger.log("Cannot get sprite from resource: " + name, e);
        }
        return null;
    }

    public static AudioClip getResourceAsAudioClip(String name) {
        name = getResourceName(name);
        Logger.log(2, "Reading audio resource: " + name);
        try {
            InputStream stream = getStream(name);
            if(stream == null) {
                throw new Exception("Stream is null. Cannot find this resource.");
            }
            BufferedInputStream bufferedStream = new BufferedInputStream(stream);
            AudioClip audioClip = new AudioClip(name, AudioSystem.getAudioInputStream(bufferedStream));
            audioClips.add(audioClip);
            return audioClip;
        } catch(Exception e) {
            Logger.log("Cannot get audio from resource: " + name, e);
        }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        name = getResourceName(name);
        if(streamResources.containsKey(name)) {
            return streamResources.get(name);
        }
        Logger.log(2, "Reading stream resource: " + name);
        try {
            InputStream stream = getStream(name);
            if(stream == null) {
                throw new Exception("Stream is null. Cannot find this resource.");
            }
            streamResources.put(name, stream);
            return stream;
        } catch(Exception e) {
            Logger.log("Cannot get stream from resource: " + name, e);
        }
        return null;
    }

    public static URL getResourceAsUrl(String name) {
        name = getResourceName(name);
        if(urlResources.containsKey(name)) {
            return urlResources.get(name);
        }
        try {
            URL url = ResourceManager.class.getResource(name);
            if(url == null) {
                throw new Exception("URL is null. Cannot find this resource.");
            }
            urlResources.put(name, url);
            return url;
        } catch(Exception e) {
            Logger.log("Cannot get URL from resource: " + name, e);
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

    public static HashMap<String, URL> getUrlResources() {
        return new HashMap<>(urlResources);
    }

    public static List<AudioClip> getAudioClips() {
        return new ArrayList<>(audioClips);
    }

    private static String getResourceName(String name) {
        if(name.charAt(1) != ':' && !name.startsWith(".")) {
            name = "/" + name;
        }
        return name;
    }

    private static InputStream getStream(String path) throws IOException {
        if(path.startsWith("/")) {
            return ResourceManager.class.getResourceAsStream(path);
        }
        return Files.newInputStream(Paths.get(path));
    }

    public static void clearResources() {
        resources.clear();
    }

    public static void clearSpriteResources() {
        spriteResources.clear();
    }

    public static void clearStreamResources() {
        streamResources.clear();
    }

    public static void clearAudioClips() {
        for(AudioClip audioClip : audioClips) {
            try {
                audioClip.getAudioStream().close();
            } catch(IOException e) {
                Logger.log("Cannot close audio stream: ", e);
            }
        }
        audioClips.clear();
    }

}
