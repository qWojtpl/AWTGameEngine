package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
        HashMap<Integer, List<GameObject>> sortedObjects = new HashMap<>();
        int maxPriority = 0;
        for(GameObject go : SceneLoader.getCurrentScene().getGameObjects()) {
            if(go.getPriority() > maxPriority) {
                maxPriority = go.getPriority();
            }
            List<GameObject> objects = sortedObjects.getOrDefault(go.getPriority(), new ArrayList<>());
            objects.add(go);
            sortedObjects.put(go.getPriority(), objects);
        }
        for(int i = 0; i <= maxPriority; i++) {
            for(GameObject go : sortedObjects.getOrDefault(i, new ArrayList<>())) {
                go.render(g);
            }
        }
        g.dispose();
    }

}
