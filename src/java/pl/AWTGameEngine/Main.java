package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.windows.WindowsManager;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.opengl", "true");
        for(String arg : args) {
            AppProperties.addStartupArgument(arg);
        }
        Logger.setEnabled(AppProperties.getPropertyAsBoolean("enableLogging"));
        Logger.setLogFile(AppProperties.getPropertyAsBoolean("logFile"));
        Logger.log("Requesting default window...");
        WindowsManager.createDefaultWindow();
        Logger.log("Started app.");
    }

}
