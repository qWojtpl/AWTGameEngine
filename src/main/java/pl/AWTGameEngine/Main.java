package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.CommandConsole;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.scenes.SceneBuilder;

import java.util.ArrayList;
import java.util.List;

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
        CommandConsole.runScanner();
    }

    private static boolean isSceneBuilder(String[] args) {
        boolean sceneBuilder = false;
        boolean sceneBuilderMode = false;
        boolean force = false;
        List<String> buildArgs = new ArrayList<>();
        for(String arg : args) {
            if(arg.startsWith("-")) {
                if(arg.equalsIgnoreCase("--build")) {
                    sceneBuilder = true;
                    if(!sceneBuilderMode) {
                        sceneBuilderMode = true;
                        Logger.info("Running in SceneBuilder mode!");
                        Logger.warning("Please note: don't build scene every time while debugging/development. " +
                                "Using SceneBuilder is intended to use it before release to provide faster loading.");
                    }
                    continue;
                } else if(arg.equalsIgnoreCase("--force")) {
                    force = true;
                    sceneBuilder = false;
                    continue;
                }
                sceneBuilder = false;
                break;
            } else if(sceneBuilder) {
                buildArgs.add(arg);
            }
        }
        if(!buildArgs.isEmpty()) {
            Logger.info("Scenes to build today: " + String.join(", ", buildArgs));
            for(String buildArg : buildArgs) {
                SceneBuilder.build(buildArg, force);
            }
            Logger.info("SceneBuilder: Done.");
        }
        return sceneBuilderMode;
    }

}
