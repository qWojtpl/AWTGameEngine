package pl.AWTGameEngine;

import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.Button;
import pl.AWTGameEngine.components.TextRenderer;
import pl.AWTGameEngine.custom.CustomComponent;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.DuplicatedObjectException;
import pl.AWTGameEngine.scenes.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends Frame {

    private static Main instance;
    private Scene currentScene;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        super("Java 2D test");
        instance = this;
        setSize(400, 300);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setCursor(Cursor.WAIT_CURSOR);
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
        addKeyListener(new KeyListener());
        currentScene = new Scene("main");
        try {
            GameObject go = currentScene.createGameObject("0");
            go.setX(100);
            go.setY(100);
            TextRenderer renderer = new TextRenderer();
            renderer.setText("test");
            go.addComponent(new BlankRenderer());
            go.addComponent(new Button());
            go.addComponent(new CustomComponent());
            go.addComponent(renderer);
        } catch(DuplicatedObjectException e) {
            e.printStackTrace();
        }
        new GameLoop().start();
    }

    public void paint(Graphics g) {
        if(g == null || currentScene == null) {
            return;
        }
        for(GameObject go : currentScene.getGameObjects()) {
            go.render();
        }
        g.dispose();
    }

    public static Main getInstance() {
        return instance;
    }

}
