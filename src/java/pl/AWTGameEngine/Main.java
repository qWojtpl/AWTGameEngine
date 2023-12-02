package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.windows.WindowsManager;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.opengl", "true");
        for(String arg : args) {
            AppProperties.addStartupArgument(arg);
        }
        WindowsManager.createDefaultWindow();
    }

}
