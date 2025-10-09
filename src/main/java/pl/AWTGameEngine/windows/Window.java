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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Window extends JFrame {

    private final boolean serverWindow;
    private boolean sameSize = false;
    private final int WIDTH = 1920;
    private BaseLoop renderLoop;
    private BaseLoop updateLoop;
    private BaseLoop physicsLoop;
    private KeyListener keyListener;
    private WindowListener windowListener;
    private final HashMap<Scene, Boolean> scenes = new HashMap<>();
    private SceneLoader sceneLoader;
    private boolean fullScreen;
    private final Font font;
    private Cursor cursor;

    public Window(boolean serverWindow) {
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

    public void unloadScenes() {
        for(Scene scene : scenes.keySet()) {
            scene.removeAllObjects();
        }
        Dependencies.getResourceManager().clearAudioClips();
        for(Scene scene : scenes.keySet()) {
            scene.getPanel().unload();
        }
        scenes.clear();
    }

    public void init() {
        if (isFullScreen() && !serverWindow) {
            setUndecorated(true);
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            device.setFullScreenWindow(this);
        }
        setSize(getBaseWidth(), getBaseHeight());
        setKeyListener(new KeyListener(this));
        setWindowListener(new WindowListener(this));
        setLayout(new BorderLayout());
        addComponentListener(getWindowListener());
        if (!serverWindow) {
            setVisible(true);
        }
    }

    public void updateRatio(int r1, int r2) {
        for(Scene scene : scenes.keySet()) {
            scene.getPanel().setSize(new Dimension(getWidth(), getWidth() * r2 / r1));
        }
    }

    public boolean isServerWindow() {
        return this.serverWindow;
    }

    public boolean isSameSize() {
        return this.sameSize;
    }

    public List<PanelObject> getPanels() {
        List<PanelObject> panels = new ArrayList<>();
        for(Scene scene : scenes.keySet()) {
            panels.add(scene.getPanel());
        }
        return panels;
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
        for(Scene scene : scenes.keySet()) {
            if(scenes.get(scene)) {
                return scene;
            }
        }
        return null;
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

    public void addScene(Scene scene) {
        scenes.putIfAbsent(scene, scenes.isEmpty());
    }

    public void setCurrentScene(Scene newCurrentScene) {
        if(scenes.getOrDefault(newCurrentScene, null) == null) {
            throw new RuntimeException("Scene must be owned by this window!");
        }
        for(Scene scene : new ArrayList<>(scenes.keySet())) {
            scenes.remove(scene);
            scenes.put(scene, scene.equals(newCurrentScene));
            scene.getPanel().setOpaque(!scene.equals(newCurrentScene));
        }
    }

    public void setSceneLoader(SceneLoader sceneLoader) {
        this.sceneLoader = sceneLoader;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        for(Scene scene : scenes.keySet()) {
            scene.getPanel().setCursor(cursor);
        }
    }

}