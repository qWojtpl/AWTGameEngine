package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Window extends JFrame {

    private double multiplier = 3;
    private final int WIDTH = 480;
    private final int HEIGHT = (int) (WIDTH * 0.5625);
    private NestedPanel panel;
    private GameLoop renderLoop;
    private GameLoop updateLoop;
    private KeyListener keyListener;
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

    public double getMultiplier() {
        return this.multiplier;
    }

    public NestedPanel getPanel() {
        return this.panel;
    }

    public GameLoop getRenderLoop() {
        return this.renderLoop;
    }

    public GameLoop getUpdateLoop() {
        return this.updateLoop;
    }

    public KeyListener getKeyListener() {
        return this.keyListener;
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

    public void setMultiplier(double multiplier) {
        if(multiplier <= 0) {
            multiplier = 1;
        }
        if(isFullScreen()) {
            multiplier = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / WIDTH);
        }
        this.multiplier = multiplier;
        if(panel != null) {
            if(getCurrentScene() != null) {
                getCurrentScene().getPanelRegistry().removePanel(panel);
            }
            remove(panel);
        }
        this.panel = new NestedPanel(this);
        add(panel);
        panel.setPreferredSize(new Dimension((int) (WIDTH * multiplier), (int) (HEIGHT * multiplier)));
        pack();
        setKeyListener(new KeyListener(this));
    }

    public void setRenderLoop(GameLoop loop) {
        this.renderLoop = loop;
    }

    public void setUpdateLoop(GameLoop loop) {
        this.updateLoop = loop;
    }

    public void setKeyListener(KeyListener listener) {
        if(this.keyListener != null) {
            removeKeyListener(this.keyListener);
        }
        this.keyListener = listener;
        addKeyListener(listener);
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