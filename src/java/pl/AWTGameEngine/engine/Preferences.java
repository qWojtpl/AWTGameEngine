package pl.AWTGameEngine.engine;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public abstract class Preferences {

    private static final HashMap<String, String> preferences = new HashMap<>();

    static {
        loadPreferences();
    }

    public static void savePreference(String key, String preference) {
        if(key == null) {
            key = "";
        }
        if(preference == null) {
            preference = "";
        }
        preferences.put(key, preference);
        writeToFile();
    }

    public static String getPreference(String key) {
        return preferences.getOrDefault(key, null);
    }

    public static String getPreference(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }

    public static void writeToFile() {
        try {
            boolean append = false;
            for(String key : preferences.keySet()) {
                FileWriter writer = new FileWriter(getPreferencesFile(), append);
                if(!append) {
                    append = true;
                }
                String preference = preferences.get(key);
                String newKey = "";
                String newPreference = "";
                for(int i = 0; i < key.length(); i++) {
                    newKey += (char) (key.charAt(i) + AppProperties.getPropertyAsInteger("preferenceShift"));
                }
                for(int i = 0; i < preference.length(); i++) {
                    newPreference += (char) (preference.charAt(i) + AppProperties.getPropertyAsInteger("preferenceShift"));
                }
                writer.write(newKey + "\n");
                writer.write(newPreference + "\n");
                writer.close();
            }
        } catch(IOException e) {
            System.out.println("Cannot create preferences file!");
        }
    }

    public static void loadPreferences() {
        preferences.clear();
        try {
            List<String> fileContent = Files.readAllLines(getPreferencesFile().toPath());
            String key = null;
            for(String line : fileContent) {
                String newLine = "";
                for(int i = 0; i < line.length(); i++) {
                    newLine += (char) (line.charAt(i) - AppProperties.getPropertyAsInteger("preferenceShift"));
                }
                if(key == null) {
                    key = newLine;
                } else {
                    preferences.put(key, newLine);
                    key = null;
                }
            }
        } catch(Exception e) {
            System.out.println("Cannot read preferences file.");
            e.printStackTrace();
        }
    }

    public static File getPreferencesFile() {
        File file = new File("preferences.txt");
        try {
            boolean c = file.createNewFile();
        } catch(IOException e) {
            System.out.println("Can't create preferences file");
            e.printStackTrace();
        }
        return file;
    }

}
