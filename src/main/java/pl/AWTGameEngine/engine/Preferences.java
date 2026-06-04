package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.annotations.tests.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;

@Command("preferences")
@Test(name = "saveAndGetPreference", testClass = "pl.AWTGameEngineTests.engine.Preferences.SaveAndGetPreferenceTest")
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
            FileWriter writer = Dependencies.getResourceManager().getWriter("preferences.bin", false);
            for(String key : preferences.keySet()) {
                if(!append) {
                    append = true;
                    writer.close();
                    writer = Dependencies.getResourceManager().getWriter("preferences.bin", true);
                }
                String preference = preferences.get(key);
                if(preference.isEmpty()) {
                    continue;
                }
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
            }
            writer.close();
        } catch(IOException e) {
            Logger.exception("Cannot create preferences file!", e);
        }
    }

    public void loadPreferences() {
        preferences.clear();
        try {
            List<String> fileContent = Dependencies.getResourceManager().getResource("./preferences.bin");
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
        Dependencies.getResourceManager().releaseFileResource("./preferences.bin");
    }

}
