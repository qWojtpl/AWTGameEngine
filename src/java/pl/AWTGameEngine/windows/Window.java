package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;

public class Window extends JFrame {

    private GamePanel panel;
    private GameLoop loop;
    private KeyListener keyListener;
    private Scene currentScene;
    private SceneLoader sceneLoader;

    public GamePanel getPanel() {
        return this.panel;
    }

    public GameLoop getLoop() {
        return this.loop;
    }

    public KeyListener getKeyListener() {
        return this.keyListener;
    }

    public Scene getCurrentScene() {
        return this.currentScene;
    }

    public SceneLoader getSceneLoader() {
        return this.sceneLoader;
    }

    public void setPanel(GamePanel panel) {
        if(this.panel != null) {
            remove(panel);
        }
        this.panel = panel;
        add(panel);
    }

    public void setLoop(GameLoop loop) {
        this.loop = loop;
    }

    public void setKeyListener(KeyListener listener) {
        if(this.keyListener != null) {
            this.removeKeyListener(this.keyListener);
        }
        this.keyListener = listener;
        this.addKeyListener(listener);
    }

    public void setCurrentScene(Scene scene) {
        this.currentScene = scene;
    }

    public void setSceneLoader(SceneLoader sceneLoader) {
        this.sceneLoader = sceneLoader;
    }

}
