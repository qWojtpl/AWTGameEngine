package pl.AWTGameEngine;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.CommandConsole;
import pl.AWTGameEngine.engine.Preferences;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.windows.WindowsManager;

@Command("dependencies")
public class Dependencies extends CommandConsole.ParentCommand {

    private static WindowsManager windowsManager = new WindowsManager();
    private static AppProperties appProperties = new AppProperties();
    private static ResourceManager resourceManager = new ResourceManager();
    private static Preferences preferences = new Preferences();

    static {
        new Dependencies();
    }

    @Command("wmanager")
    public static WindowsManager getWindowsManager() {
        if(windowsManager == null) {
            windowsManager = new WindowsManager();
        }
        return windowsManager;
    }

    @Command("props")
    public static AppProperties getAppProperties() {
        if(appProperties == null) {
            appProperties = new AppProperties();
        }
        return appProperties;
    }

    @Command("resource")
    public static ResourceManager getResourceManager() {
        if(resourceManager == null) {
            resourceManager = new ResourceManager();
        }
        return resourceManager;
    }

    @Command("preferences")
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
