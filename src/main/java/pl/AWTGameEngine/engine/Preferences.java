package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Command;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@Command("preferences")
public class Preferences extends CommandConsole.ParentCommand {

    private final HashMap<String, String> preferences = new HashMap<>();

    public Preferences() {
        loadPreferences();
    }

    @Command(value = "save", argumentNames = { "key", "value" })
    public void savePreference(String key, String preference) {
        if(key == null) {
            key = "";
        }
        if(preference == null) {
            preference = "";
        }
        preferences.put(key, preference);
        writeToFile();
    }

    @Command(value = "get", argumentNames = "key")
    public String getPreference(String key) {
        return preferences.getOrDefault(key, null);
    }

    public String getPreference(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }

    public void writeToFile() {
        AppProperties appProperties = Dependencies.getAppProperties();
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
                    newKey.append((char) (key.charAt(i) + appProperties.getPropertyAsInteger("preferenceShift")));
                }
                for(int i = 0; i < preference.length(); i++) {
                    newPreference.append((char) (preference.charAt(i) + appProperties.getPropertyAsInteger("preferenceShift")));
                }
                writer.write(newKey + "\n");
                writer.write(newPreference + "\n");
                writer.close();
            }
        } catch(IOException e) {
            Logger.exception("Cannot create preferences file!", e);
        }
    }

    public void loadPreferences() {
        preferences.clear();
        try {
            List<String> fileContent = Files.readAllLines(getPreferencesFile().toPath());
            String key = null;
            for(String line : fileContent) {
                StringBuilder newLine = new StringBuilder();
                for(int i = 0; i < line.length(); i++) {
                    newLine.append((char) (line.charAt(i) - Dependencies.getAppProperties().getPropertyAsInteger("preferenceShift")));
                }
                if(key == null) {
                    key = newLine.toString();
                } else {
                    preferences.put(key, newLine.toString());
                    key = null;
                }
            }
        } catch(Exception e) {
            Logger.exception("Cannot read preferences file.", e);
        }
    }

    public File getPreferencesFile() {
        File file = new File("preferences.bin");
        try {
            file.createNewFile();
        } catch(IOException e) {
            Logger.exception("Can't create preferences file", e);
        }
        return file;
    }

}
