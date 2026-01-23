package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Preferences;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.windows.WindowsManager;

public class Dependencies {

    private static WindowsManager windowsManager;
    private static AppProperties appProperties;
    private static ResourceManager resourceManager;
    private static Preferences preferences;

    public static WindowsManager getWindowsManager() {
        if(windowsManager == null) {
            windowsManager = new WindowsManager();
        }
        return windowsManager;
    }

    public static AppProperties getAppProperties() {
        if(appProperties == null) {
            appProperties = new AppProperties();
        }
        return appProperties;
    }

    public static ResourceManager getResourceManager() {
        if(resourceManager == null) {
            resourceManager = new ResourceManager();
        }
        return resourceManager;
    }

    public static Preferences getPreferences() {
        if(preferences == null) {
            preferences = new Preferences();
        }
        return preferences;
    }

    public static void setWindowsManager(WindowsManager windowsManager) {
        Dependencies.windowsManager = windowsManager;
    }

    public static void setAppProperties(AppProperties appProperties) {
        Dependencies.appProperties = appProperties;
    }

    public static void setResourceManager(ResourceManager resourceManager) {
        Dependencies.resourceManager = resourceManager;
    }

    public static void setPreferences(Preferences preferences) {
        Dependencies.preferences = preferences;
    }

}
