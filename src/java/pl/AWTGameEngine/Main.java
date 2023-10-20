package pl.AWTGameEngine;

import pl.AWTGameEngine.engine.Preferences;
import pl.AWTGameEngine.windows.WindowsManager;

public class Main {

    public static void main(String[] args) {
        Preferences.savePreference("playerName", "Assasin98980");
        System.setProperty("sun.java2d.uiScale", "1");
        WindowsManager.createDefaultWindow();
    }

}
