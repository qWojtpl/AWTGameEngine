package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.opengl", "true");
        AppProperties appProperties = Dependencies.getAppProperties();
        for(String arg : args) {
            appProperties.addStartupArgument(arg);
        }
        Logger.setLevel(appProperties.getPropertyAsInteger("logLevel"));
        Logger.setLogFile(appProperties.getPropertyAsBoolean("logFile"));
        Logger.setCallerClass(appProperties.getPropertyAsBoolean("logCallerClass"));
        Logger.log(2, "Requesting default window...");
        Dependencies.getWindowsManager().createDefaultWindow();
        Logger.log(2, "Started app.");
    }

}
