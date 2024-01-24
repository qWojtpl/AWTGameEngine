package pl.AWTGameEngine.engine;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class Preferences {

    private static final HashMap<String, String> preferences = new HashMap<>();

    Preferences() {

    }

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
                StringBuilder newKey = new StringBuilder();
                StringBuilder newPreference = new StringBuilder();
                for(int i = 0; i < key.length(); i++) {
                    newKey.append((char) (key.charAt(i) + AppProperties.getPropertyAsInteger("preferenceShift")));
                }
                for(int i = 0; i < preference.length(); i++) {
                    newPreference.append((char) (preference.charAt(i) + AppProperties.getPropertyAsInteger("preferenceShift")));
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
                StringBuilder newLine = new StringBuilder();
                for(int i = 0; i < line.length(); i++) {
                    newLine.append((char) (line.charAt(i) - AppProperties.getPropertyAsInteger("preferenceShift")));
                }
                if(key == null) {
                    key = newLine.toString();
                } else {
                    preferences.put(key, newLine.toString());
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
