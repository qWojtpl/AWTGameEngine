package pl.AWTGameEngine;

import pl.AWTGameEngine.windows.WindowsManager;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.opengl", "true");
        WindowsManager.createDefaultWindow();
        WindowsManager.createWindow("scenes/main.scene");
    }

}
