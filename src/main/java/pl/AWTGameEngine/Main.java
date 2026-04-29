package pl.AWTGameEngine;

import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.MemoryStack;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.CommandConsole;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.VRManager;
import pl.AWTGameEngine.scenes.SceneBuilder;

import java.nio.IntBuffer;

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
        Logger.redirectJULLogger();
        if(SceneBuilder.isSceneBuilder(args)) {
            return;
        }
        if(appProperties.isStartupArgumentPresent("--vr")) {
            VRManager.getInstance().init();
        }
        Logger.info("Requesting default window...");
        Dependencies.getWindowsManager().createDefaultWindow();
        Logger.info("Started app.");
        CommandConsole.runScanner();
    }

}
