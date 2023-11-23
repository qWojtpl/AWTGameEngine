package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;
import pl.AWTGameEngine.components.TextArea;
import pl.AWTGameEngine.components.Button;

import javax.swing.*;
import java.awt.*;

public class DialogManager {

    static {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("OptionPane.minimumSize", new Dimension(500,300));
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

    public static void createError(String message) {
        Window w = WindowsManager.createWindow(AppProperties.getProperty("error"));
        GameObject go = w.getCurrentScene().getGameObjectByName("@textArea");
        ((TextArea) go.getComponentsByClass(TextArea.class).get(0)).setText(message);
    }

    public static void createExtendedError(Window window, String windowTitle, String message, String errorMessage) {
/*        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Label label = new Label(message);
        panel.add(label);
        label.setFont(window.getFont().deriveFont(16f));
        TextArea textArea = new TextArea(errorMessage);
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(100, 100));
        textArea.setFont(window.getFont().deriveFont(14f));
        panel.add(textArea);
        JOptionPane.showMessageDialog(
                window,
                panel,
                windowTitle,
                JOptionPane.PLAIN_MESSAGE);*/
    }

}
