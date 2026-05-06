package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.ColorObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * SplashScreenWindow is used for create a splash screen, which will be shown
 * as a loader screen while main window (with scene) is still loading.
 */
@SuppressWarnings("deprecation")
public class SplashScreenWindow extends JFrame {

    private int width;
    private int height;
    private int rows;
    private int columns;

    public void init() {
        AppProperties appProperties = Dependencies.getAppProperties();
        width = appProperties.getPropertyAsInteger("splashScreenWidth");
        height = appProperties.getPropertyAsInteger("splashScreenHeight");
        columns = appProperties.getPropertyAsInteger("splashScreenColumns");
        rows = appProperties.getPropertyAsInteger("splashScreenRows");
        setSize(width, height);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setCursor(Cursor.WAIT_CURSOR);
        GridLayout grid = new GridLayout(rows, columns);
        setBackground(ColorObject.deserialize(appProperties.getProperty("splashScreenColor")));
        setLayout(grid);
        List<JLabel> logLabels = new ArrayList<>();
        String content = appProperties.getProperty("splashScreenContent");
        String[] split = content.split(",", -1);
        if(split.length != columns * rows) {
            throw new RuntimeException("Cannot show a splash screen - incorrect content.");
        }
        for(int i = 0; i < columns * rows; i++) {
            if(split[i].startsWith("/")) {
                addLogo(split[i]);
            } else {
                String[] sizeSplit = split[i].split("\\*");
                JLabel label;
                if(sizeSplit.length != 1) {
                    label = addLabel(sizeSplit[0], Integer.parseInt(sizeSplit[1]));
                } else {
                    label = addLabel(split[i], 0);
                }
                if(split[i].contains("{LOGGER}")) {
                    logLabels.add(label);
                }
            }
        }
        setVisible(true);
        new Thread(() -> {
            while(isVisible()) {
                for(JLabel logLabel : logLabels) {
                    logLabel.setText(Logger.getLastLog());
                }
                try {
                    //noinspection BusyWait
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "SPLASHSCREEN-LOG-LABEL").start();
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private JLabel addLabel(String text, float fontSize) {
        JLabel label = new JLabel(text);
        if(fontSize != 0) {
            label.setFont(label.getFont().deriveFont(fontSize));
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setSize(width / columns, height / rows);
        add(label);
        return label;
    }

    private void addLogo(String source) {
        try {
            InputStream stream = SplashScreenWindow.class.getResourceAsStream(source);
            if(stream == null) {
                return;
            }
            BufferedImage logo = ImageIO.read(stream);

            int cellWidth = width / columns;
            int cellHeight = height / rows;
            float scale = Math.min(
                    (float) cellWidth / logo.getWidth(),
                    (float) cellHeight / logo.getHeight()
            );
            int newWidth = (int) (logo.getWidth() * scale);
            int newHeight = (int) (logo.getHeight() * scale);

            Image scaledImage = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            JLabel picLabel = new JLabel(new ImageIcon(scaledImage));
            picLabel.setHorizontalAlignment(SwingConstants.CENTER);
            picLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(picLabel);
            stream.close();
        } catch(IOException e) {
            // ignore
        }
    }

}
