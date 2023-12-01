package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Window extends JFrame {

    private GamePanel panel;
    private GameLoop loop;
    private KeyListener keyListener;
    private MouseListener mouseListener;
    private WindowListener windowListener;
    private Scene currentScene;
    private SceneLoader sceneLoader;
    private boolean staticMode;
    private boolean fullScreen;
    private final Font defaultFont;

    public Window() {
        defaultFont = new Font(
            AppProperties.getProperty("font"),
            Font.PLAIN,
            AppProperties.getPropertyAsInteger("fontSize")
        );
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

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

    public WindowListener getWindowListener() {
        return this.windowListener;
    }

    public Scene getCurrentScene() {
        return this.currentScene;
    }

    public SceneLoader getSceneLoader() {
        return this.sceneLoader;
    }

    public Font getDefaultFont() {
        return this.defaultFont;
    }

    public Font getDefaultFont(float size) {
        return getDefaultFont().deriveFont(size);
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
            getPanel().removeMouseMotionListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        getPanel().addMouseListener(mouseListener);
        getPanel().addMouseMotionListener(mouseListener);
    }

    public void setWindowListener(WindowListener windowListener) {
        if(this.windowListener != null) {
            removeWindowListener(this.windowListener);
        }
        this.windowListener = windowListener;
        addWindowListener(windowListener);
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