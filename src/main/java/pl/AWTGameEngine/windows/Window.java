package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.engine.panels.*;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Window extends JFrame {

    private RenderEngine renderEngine;
    private final boolean serverWindow;
    private boolean sameSize = false;
    private final int WIDTH = 1920;
    private DefaultPanel panel;
    private WebPanel webPanel;
    private PanelFX threeDimensionalPanel;
    private PanelGL openGlPanel;
    private BaseLoop renderLoop;
    private BaseLoop updateLoop;
    private BaseLoop physicsLoop;
    private KeyListener keyListener;
    private WindowListener windowListener;
    private Scene currentScene;
    private SceneLoader sceneLoader;
    private boolean staticMode;
    private boolean fullScreen;
    private final Font font;
    private Cursor cursor;

    public Window(RenderEngine renderEngine, boolean serverWindow) {
        this.renderEngine = renderEngine;
        this.serverWindow = serverWindow;
        AppProperties appProperties = Dependencies.getAppProperties();
        font = new Font(
                appProperties.getProperty("font"),
                Font.PLAIN,
                appProperties.getPropertyAsInteger("fontSize")
        );
        getContentPane().setBackground(Color.BLACK);
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void unloadScene() {
        currentScene.removeAllObjects();
        setCurrentScene(null);
        Dependencies.getResourceManager().clearAudioClips();
        getPanel().unload();
        if (getPanel() instanceof DefaultPanel) {
            remove(panel);
            panel = null;
        } else if (getPanel() instanceof WebPanel) {
            remove(webPanel);
            webPanel = null;
        } else if (getPanel() instanceof PanelFX) {
            remove(threeDimensionalPanel);
            threeDimensionalPanel = null;
        } else if (getPanel() instanceof PanelGL) {
            remove(openGlPanel);
            openGlPanel = null;
        }
    }

    private void createPanel(int width, int height) {
        if (RenderEngine.DEFAULT.equals(renderEngine)) {
            if (this.panel == null) {
                this.panel = new DefaultPanel(this);
                add(panel, BorderLayout.CENTER);
            }
        } else if (RenderEngine.WEB.equals(renderEngine)) {
            if (this.webPanel == null) {
                this.webPanel = new WebPanel(this);
                add(webPanel, BorderLayout.CENTER);
            }
        } else if (RenderEngine.FX3D.equals(renderEngine)) {
            if (this.threeDimensionalPanel == null) {
                this.threeDimensionalPanel = new PanelFX(this, width, height);
                add(threeDimensionalPanel, BorderLayout.CENTER);
            }
        } else if (RenderEngine.OPENGL.equals(renderEngine)) {
            if (this.openGlPanel == null) {
                this.openGlPanel = new PanelGL(this, width, height);
                add(openGlPanel, BorderLayout.CENTER);
            }
        }
        getPanel().setSize(new Dimension(width, height));
    }

    public void init() {
        if (isFullScreen() && !serverWindow) {
            setUndecorated(true);
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            device.setFullScreenWindow(this);
        }
        setSize(getBaseWidth(), getBaseHeight());
        createPanel(getBaseWidth(), getBaseHeight());
        setKeyListener(new KeyListener(this));
        setWindowListener(new WindowListener(this));
        setLayout(new BorderLayout());
        addComponentListener(getWindowListener());
        if (!serverWindow) {
            setVisible(true);
        }
    }

    public void updateRatio(int r1, int r2) {
        getPanel().setSize(new Dimension(getWidth(), getWidth() * r2 / r1));
    }

    public RenderEngine getRenderEngine() {
        return this.renderEngine;
    }

    public boolean isServerWindow() {
        return this.serverWindow;
    }

    public boolean isSameSize() {
        return this.sameSize;
    }

    public PanelObject getPanel() {
        if (this.threeDimensionalPanel != null) {
            return this.threeDimensionalPanel;
        }
        if (this.openGlPanel != null) {
            return this.openGlPanel;
        }
        if (this.webPanel != null) {
            return this.webPanel;
        }
        return this.panel;
    }

    public BaseLoop getRenderLoop() {
        return this.renderLoop;
    }

    public BaseLoop getUpdateLoop() {
        return this.updateLoop;
    }

    public BaseLoop getPhysicsLoop() {
        return this.physicsLoop;
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

    public void setRenderLoop(BaseLoop loop) {
        this.renderLoop = loop;
    }

    public void setUpdateLoop(BaseLoop loop) {
        this.updateLoop = loop;
    }

    public void setPhysicsLoop(BaseLoop loop) {
        this.physicsLoop = loop;
    }

    public void setKeyListener(KeyListener listener) {
        if (this.keyListener != null) {
            removeKeyListener(this.keyListener);
        }
        this.keyListener = listener;
        addKeyListener(listener);
    }

    public void setWindowListener(WindowListener windowListener) {
        if (this.windowListener != null) {
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
        if (RenderEngine.FX3D.equals(renderEngine)) {
            threeDimensionalPanel.setCursor(cursor);
        } else if (RenderEngine.OPENGL.equals(renderEngine)) {
            openGlPanel.setCursor(cursor);
        } else if (RenderEngine.WEB.equals(renderEngine)) {
            webPanel.setCursor(cursor);
        } else {
            panel.setCursor(cursor);
        }
    }

    public enum RenderEngine {

        DEFAULT,
        WEB,
        FX3D,
        OPENGL

    }

}