package pl.AWTGameEngine;

import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.TextRenderer;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Frame {

    private static Main instance;
    private List<GameObject> gameObjects = new ArrayList<>();

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        super("Java 2D test");
        instance = this;
        setSize(400, 300);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setCursor(Cursor.WAIT_CURSOR);
        setUndecorated(true);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose();
                System.out.println("Window exit.");
                System.exit(0);
            }
        });
        new GameLoop().start();
        new FPSCounter().start();
        GameObject go = new GameObject();
        go.getComponents().add(new BlankRenderer());
        TextRenderer textRenderer = new TextRenderer();
        textRenderer.setText("test");
        GameObject go2 = new GameObject();
        go2.getComponents().add(new TextRenderer());
        gameObjects.add(go);
        go2.setX(300);
        go2.setY(300);
        gameObjects.add(go2);
    }

    public void paint(Graphics g) {
        for(GameObject go : gameObjects) {
            go.render();
        }
        g.dispose();
    }

    public static Main getInstance() {
        return instance;
    }

}
