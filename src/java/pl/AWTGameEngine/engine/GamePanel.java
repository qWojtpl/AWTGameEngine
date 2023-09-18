package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    public GamePanel() {
        this.setPreferredSize(new Dimension(1200, 800));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(g == null || SceneLoader.getCurrentScene() == null) {
            return;
        }
        for(GameObject go : SceneLoader.getCurrentScene().getGameObjects()) {
            go.render(g);
        }
        g.dispose();
    }

}
