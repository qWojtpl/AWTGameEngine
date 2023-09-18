package pl.AWTGameEngine;

import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.Button;
import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.components.TextRenderer;
import pl.AWTGameEngine.custom.CustomComponent;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.DuplicatedObjectException;
import pl.AWTGameEngine.scenes.Scene;

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
        currentScene = new Scene("main");
        Image img = null;
        try {
            img = ImageIO.read(new File("C:/Users/wojto/OneDrive/Obrazy/0193fb7685c8d7b0f3a204a85b135af0 (1).jpg"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        for(int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                try {
                    GameObject go = currentScene.createGameObject(x + "x" + y);
                    go.setX(100 + x * 100);
                    go.setY(100 + y * 100);
                    SpriteRenderer renderer = new SpriteRenderer();
                    renderer.setImage(img);
                    go.addComponent(renderer);
                    go.addComponent(new CustomComponent());
                    //go.addComponent(new BlankRenderer());
                } catch (DuplicatedObjectException e) {
                    e.printStackTrace();
                }
            }
        }
        new GameLoop().start();
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static JFrame getWindow() {
        return window;
    }

    public static GamePanel getPanel() {
        return panel;
    }

}
