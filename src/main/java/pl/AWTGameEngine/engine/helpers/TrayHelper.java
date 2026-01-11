package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;

public class TrayHelper {

    public static void displayTray(String title, String message, TrayIcon.MessageType messageType) {
        displayTray(null, title, message, messageType);
    }

    public static void displayTray(Sprite sprite, String title, String message, TrayIcon.MessageType messageType) {
        if(!SystemTray.isSupported()) {
            Logger.error("Cannot display Tray, because trays aren't supported in this system!");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon icon = new TrayIcon(sprite != null ? sprite.getImage() : Toolkit.getDefaultToolkit().createImage("icon.png"), title);
        icon.setImageAutoSize(true);
        try {
            tray.add(icon);
            icon.displayMessage(title, message, messageType);
        } catch (AWTException e) {
            Logger.exception("Cannot display Tray", e);
        }

    }

}
