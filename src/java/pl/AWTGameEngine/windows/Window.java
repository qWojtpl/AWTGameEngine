package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;

public class Window extends JFrame {

    private GamePanel panel;
    private GameLoop loop;
    private KeyListener keyListener;
    private MouseListener mouseListener;
    private Scene currentScene;
    private SceneLoader sceneLoader;
    private boolean staticMode;
    private boolean fullScreen;

    public GamePanel getPanel() {
        return this.panel;
    }

    public GameLoop getLoop() {
        return this.loop;
    }

    public KeyListener getKeyListener() {
        return this.keyListener;
    }

    public MouseListener getMouseListener() {
        return this.mouseListener;
    }

    public Scene getCurrentScene() {
        return this.currentScene;
    }

    public SceneLoader getSceneLoader() {
        return this.sceneLoader;
    }

    public boolean isStaticMode() {
        return this.staticMode;
    }

    public boolean isFullScreen() {
        return this.fullScreen;
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

    public void setMouseListener(MouseListener mouseListener) {
        if(this.mouseListener != null) {
            getPanel().removeMouseListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        getPanel().addMouseListener(mouseListener);
    }

    public void setCurrentScene(Scene scene) {
        this.currentScene = scene;
    }

    public void setSceneLoader(SceneLoader sceneLoader) {
        this.sceneLoader = sceneLoader;
    }

    public void setStaticMode(boolean staticMode) {
        this.staticMode = staticMode;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

}