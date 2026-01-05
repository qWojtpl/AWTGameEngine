package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.TextRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * SplashScreenWindow is used for create a splash screen, which will be shown
 * as a loader screen while main window (with scene) is still loading.
 */
@SuppressWarnings("deprecation")
public class SplashScreenWindow extends JFrame {

    private final int width = 400;
    private final int height = 200;
    private final int rows = 2;
    private final int columns = 3;

    public void init() {
        setSize(width, height);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setCursor(Cursor.WAIT_CURSOR);
        GridLayout grid = new GridLayout(rows, columns);
        setLayout(grid);
        addLabel("");
        addLabel("Loading", 20);
        addLabel("");
        addLabel("");
        addLogo("/sprites/base/opengl_logo.png");
        addLabel("");
        setVisible(true);
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void addLabel(String text) {
        addLabel(text, 0);
    }

    private void addLabel(String text, float fontSize) {
        JLabel label = new JLabel(text);
        if(fontSize != 0) {
            label.setFont(label.getFont().deriveFont(fontSize));
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setSize(width / columns, height / rows);
        add(label);
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
