package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;

public class DialogManager {

    static {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
    }

    public static String createInput(Window window, String windowTitle, String message, String defaultValue) {
        return (String) JOptionPane.showInputDialog(
                window,
                message,
                windowTitle,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultValue);
    }

    public static String createSelectionInput(Window window, String windowTitle, String message, String[] options) {
        return (String) JOptionPane.showInputDialog(
                window,
                message,
                windowTitle,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null);
    }

    public static void createError(Window window, String windowTitle, String message) {
        JOptionPane.showMessageDialog(
                window,
                message,
                windowTitle,
                JOptionPane.ERROR_MESSAGE);
    }

}
