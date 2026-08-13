package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.helpers.SteamHelper;
import pl.AWTGameEngine.engine.steam.SteamManager;
import pl.AWTGameEngine.engine.tests.TestPerformer;
import pl.AWTGameEngine.scenes.SceneBuilder;

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
        Logger.setLogFileName(appProperties.getProperty("logFileName"));
        Logger.setCallerClass(appProperties.getPropertyAsBoolean("logCallerClass"));
        Logger.redirectJULLogger();
        if(TestPerformer.isRunningTests(args)) {
            return;
        }
        if(SceneBuilder.isSceneBuilder(args)) {
            return;
        }
        if(SteamHelper.canInitSteam()) {
            SteamManager.getInstance().init();
        }
        Logger.info("Requesting default window...");
        Dependencies.getWindowsManager().createDefaultWindow();
        Logger.info("Started app.");
        CommandConsole.runScanner();
    }

}
