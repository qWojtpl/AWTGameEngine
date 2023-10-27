package pl.AWTGameEngine;

import pl.AWTGameEngine.windows.WindowsManager;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        WindowsManager.createDefaultWindow();
    }

}
