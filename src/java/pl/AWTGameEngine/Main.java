package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.windows.WindowsManager;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.opengl", "true");
        for(String arg : args) {
            AppProperties.addStartupArgument(arg);
        }
        Logger.setLevel(AppProperties.getPropertyAsInteger("logLevel"));
        Logger.setLogFile(AppProperties.getPropertyAsBoolean("logFile"));
        Logger.setCallerClass(AppProperties.getPropertyAsBoolean("logCallerClass"));
        ResourceManager.setResourceCaching(AppProperties.getPropertyAsBoolean("resourceCaching"));
        Logger.log(2, "Requesting default window...");
        WindowsManager.createDefaultWindow();
        Logger.log(2, "Started app.");
    }

}
