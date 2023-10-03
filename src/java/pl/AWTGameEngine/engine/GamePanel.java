package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.SceneLoader;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private final int WIDTH = 480;
    private final int HEIGHT = (int) (WIDTH * 0.5625);
    private final Window window;
    private int multipler = 3;

    public GamePanel(Window window) {
        super(new GridBagLayout());
        this.window = window;
        if(window.isFullScreen()) {
            multipler = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / WIDTH);
        }
        this.setPreferredSize(new Dimension(WIDTH * multipler, HEIGHT * multipler));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(g == null || window.getCurrentScene() == null) {
            return;
        }
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        for(int i : sortedObjects.keySet()) {
            System.out.println(i);
            for(GameObject go : sortedObjects.get(i)) {
                go.render(g);
            }
        }
        g.dispose();
    }

    public int getMultipler() {
        return this.multipler;
    }

}
