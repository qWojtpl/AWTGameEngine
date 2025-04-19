package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.engine.panels.NestedPanel;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Window extends JFrame {

    private final RenderEngine renderEngine;
    private boolean sameSize = false;
    private final int WIDTH = 480;
    private NestedPanel panel;
    private WebPanel webPanel;
    private Panel3D threeDimensionalPanel;
    private GameLoop renderLoop;
    private GameLoop updateLoop;
    private KeyListener keyListener;
    private WindowListener windowListener;
    private Scene currentScene;
    private SceneLoader sceneLoader;
    private boolean staticMode;
    private boolean fullScreen;
    private final Font font;
    private Cursor cursor;

    public Window(RenderEngine renderEngine) {
        this.renderEngine = renderEngine;
        AppProperties appProperties = Dependencies.getAppProperties();
        font = new Font(
            appProperties.getProperty("font"),
            Font.PLAIN,
            appProperties.getPropertyAsInteger("fontSize")
        );
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void unloadScene() {
        currentScene.removeAllObjects();
        setCurrentScene(null);
        Dependencies.getResourceManager().clearAudioClips();
        getPanel().unload();
        if(getPanel() instanceof NestedPanel) {
            remove(panel);
            panel = null;
        } else if(getPanel() instanceof WebPanel) {
            remove(webPanel);
            webPanel = null;
        } else if(getPanel() instanceof Panel3D) {
            remove(threeDimensionalPanel);
            threeDimensionalPanel = null;
        }
    }

    private void createPanel(int width, int height) {
        if(RenderEngine.DEFAULT.equals(renderEngine)) {
            if(this.panel == null) {
                this.panel = new NestedPanel(this);
                add(panel);
            }
        } else if(RenderEngine.WEB.equals(renderEngine)) {
            if(this.webPanel == null) {
                this.webPanel = new WebPanel(this);
                add(webPanel);
            }
        } else if(RenderEngine.FX3D.equals(renderEngine)) {
            if(this.threeDimensionalPanel == null) {
                this.threeDimensionalPanel = new Panel3D(this, width, height);
                add(threeDimensionalPanel);
            }
        }
        getPanel().setPreferredSize(new Dimension(width, height));
    }

    public void init() {
        if(isFullScreen()) {
            setUndecorated(true);
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            device.setFullScreenWindow(this);
        }
        createPanel(getBaseWidth(), getBaseHeight());
        setKeyListener(new KeyListener(this));
        setWindowListener(new WindowListener(this));
        addComponentListener(getWindowListener());
        setVisible(true);
        pack();
    }

    public RenderEngine getRenderEngine() {
        return this.renderEngine;
    }

    public boolean isSameSize() {
        return this.sameSize;
    }

    public PanelObject getPanel() {
        if(this.threeDimensionalPanel != null) {
            return this.threeDimensionalPanel;
        }
        if(this.webPanel != null) {
            return this.webPanel;
        }
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

    public Font getFont() {
        return this.font;
    }

    public Font getFont(float size) {
        return getFont().deriveFont(size);
    }

    public boolean isStaticMode() {
        return this.staticMode;
    }

    public boolean isFullScreen() {
        return this.fullScreen;
    }

    public int getBaseWidth() {
        return WIDTH;
    }

    public int getBaseHeight() {
        return (int) (WIDTH * 0.5625);
    }

    public Cursor getCursor() {
        return this.cursor;
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

    public void setSameSize(boolean sameSize) {
        this.sameSize = sameSize;
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

    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        if(RenderEngine.FX3D.equals(renderEngine)) {
            threeDimensionalPanel.setCursor(cursor);
        } else if(RenderEngine.WEB.equals(renderEngine)) {
            webPanel.setCursor(cursor);
        } else {
            panel.setCursor(cursor);
        }
    }

    public enum RenderEngine {

        DEFAULT,
        WEB,
        FX3D

    }

}