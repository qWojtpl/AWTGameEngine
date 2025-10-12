package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
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
        Logger.setCallerClass(appProperties.getPropertyAsBoolean("logCallerClass"));
        if(isSceneBuilder(args)) {
            return;
        }
        Logger.info("Requesting default window...");
        Dependencies.getWindowsManager().createDefaultWindow();
        Logger.info("Started app.");
    }

    private static boolean isSceneBuilder(String[] args) {
        boolean sceneBuilder = false;
        for(String arg : args) {
            if(arg.startsWith("-")) {
                if(arg.equals("--build")) {
                    sceneBuilder = true;
                    Logger.warning("Please note: don't build scene every time while debugging/development. " +
                            "Using SceneBuilder is intended to use it before release to provide faster loading.");
                    continue;
                }
                break;
            } else {
                SceneBuilder.build(arg);
            }
        }
        return sceneBuilder;
    }

}
