package pl.AWTGameEngine;

import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.custom.CustomComponent;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.DuplicatedObjectException;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {

    private static Scene currentScene;
    private static JFrame window;
    private static GamePanel panel;

    public static void main(String[] args) {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setResizable(false);
        window.setTitle("Java 2D test");
        panel = new GamePanel();
        window.add(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.addKeyListener(new KeyListener());
        SceneLoader.loadScene("main");
        new GameLoop().start();
        System.console();
    }

    public static JFrame getWindow() {
        return window;
    }

    public static GamePanel getPanel() {
        return panel;
    }

}
