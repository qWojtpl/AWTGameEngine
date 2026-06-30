package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.exceptions.TrayNotSupportedException;
import pl.AWTGameEngine.objects.render.Sprite;

import java.awt.*;

public class TrayHelper {

    public static void displayTray(String title, String message, TrayIcon.MessageType messageType) {
        displayTray(null, title, message, messageType);
    }

    public static void displayTray(Sprite sprite, String title, String message, TrayIcon.MessageType messageType) {
        if(!SystemTray.isSupported()) {
            throw new TrayNotSupportedException();
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
