package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.listeners.AWTListener;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.engine.panels.*;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Window extends Frame {

    private final boolean serverWindow;
    private boolean sameSize = false;
    private final int WIDTH = 1920;
    private BaseLoop renderLoop;
    private BaseLoop updateLoop;
    private BaseLoop physicsLoop;
    private BaseLoop netLoop;
    private MouseListener mouseListener;
    private KeyListener keyListener;
    private WindowListener windowListener;
    private final HashMap<Scene, Boolean> scenes = new HashMap<>();
    private SceneLoader sceneLoader;
    private boolean fullScreen;
    private final Font font;
    private Cursor cursor;
    private Robot robot;
    private double screenWidth;
    private double screenHeight;
    private final List<Dialog> dialogs = new ArrayList<>();

    public Window(boolean serverWindow) {
        this.serverWindow = serverWindow;
        AppProperties appProperties = Dependencies.getAppProperties();
        font = new Font(
                appProperties.getProperty("font"),
                Font.PLAIN,
                appProperties.getPropertyAsInteger("fontSize")
        );
        setBackground(Color.BLACK);
    }

    public void moveMouseTo(double x, double y) {
        if(!isFocused() || !isVisible()) {
            return;
        }
        if(robot == null) {
            initRobot();
        }
        robot.mouseMove((int) x, (int) y);
    }

    public void moveMouseToCenter() {
        if(!isVisible()) {
            return;
        }
        if(robot == null) {
            initRobot();
        }
        double[] screenCenter = getScreenCenter();
        moveMouseTo(screenCenter[0], screenCenter[1]);
    }

    public double[] getScreenCenter() {
        return new double[]{screenWidth / 2, screenHeight / 2};
    }

    private void initRobot() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            this.robot = new Robot(device);
        } catch(Exception e) {
            Logger.exception("Error while initializing mouse moving Robot", e);
        }
        Rectangle bounds = device.getConfigurations()[0].getBounds();
        screenWidth = bounds.getWidth();
        screenHeight = bounds.getHeight();
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void unloadScenes() {
        Logger.info("Unloading all scenes...");
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
        setLocationRelativeTo(null);
        setMouseListener(new MouseListener(this));
        setKeyListener(new KeyListener(this));
        setWindowListener(new WindowListener(this));
        addComponentListener(getWindowListener());
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

    @Command("renderLoop")
    public BaseLoop getRenderLoop() {
        return this.renderLoop;
    }

    @Command("updateLoop")
    public BaseLoop getUpdateLoop() {
        return this.updateLoop;
    }

    @Command("physicsLoop")
    public BaseLoop getPhysicsLoop() {
        return this.physicsLoop;
    }

    @Command("netLoop")
    public BaseLoop getNetLoop() {
        return this.netLoop;
    }

    public MouseListener getMouseListener() {
        return this.mouseListener;
    }

    public KeyListener getKeyListener() {
        return this.keyListener;
    }

    public WindowListener getWindowListener() {
        return this.windowListener;
    }

    @Command("currentScene")
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

    public List<Scene> getScenes() {
        return new ArrayList<>(scenes.keySet());
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

    public List<Dialog> getDialogs() {
        return new ArrayList<>(dialogs);
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

    public void setNetLoop(BaseLoop loop) {
        this.netLoop = loop;
    }

    public void setMouseListener(MouseListener listener) {
        if(this.mouseListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(mouseListener.getAWTListener());
        }
        this.mouseListener = listener;
        AWTListener awtListener = new AWTListener(mouseListener);
        mouseListener.setAWTListener(awtListener);
        Toolkit.getDefaultToolkit().addAWTEventListener(awtListener,
                AWTEvent.MOUSE_EVENT_MASK |
                        AWTEvent.MOUSE_MOTION_EVENT_MASK |
                        AWTEvent.MOUSE_WHEEL_EVENT_MASK
        );
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
        if(dialogs.size() != scenes.keySet().size() - 1) {
            Dialog dialog = new Dialog(this, false);
            dialog.setUndecorated(true);
            dialog.setBackground(new Color(100, 0, 0));
            dialog.setVisible(true);
            dialog.setFocusable(false);
            dialogs.add(dialog);
            updateDialogs();
        }
    }

    public void updateDialogs() {
        for(Dialog d : dialogs) {
            if(getWidth() == 0 || getHeight() == 0) {
                d.setBounds(getX(), getY(), getBaseWidth(), getBaseHeight());
            } else {
                d.setBackground(new Color(0, 0, 0, 0));
                d.setBounds(getX() + getInsets().left, getY() + getInsets().top, getWidth(), getHeight());
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        updateDialogs();
    }

    public void setCurrentScene(Scene newCurrentScene) {
        if(scenes.getOrDefault(newCurrentScene, null) == null) {
            throw new RuntimeException("Scene must be owned by this window!");
        }
        int i = 0;
        removeAll();
        List<Scene> sceneList = new ArrayList<>(scenes.keySet());
        for(Scene scene : sceneList) {
            scenes.replace(scene, scene.equals(newCurrentScene));
            scene.getPanel().setOpaque(scene.equals(newCurrentScene));
            if(scene.equals(newCurrentScene) && scenes.keySet().size() > 1) {
                if(scene.getPanel() instanceof PanelGL) {
                    add(((PanelGL) scene.getPanel()).getGljPanel());
                    ((PanelGL) scene.getPanel()).getGljPanel().setFocusable(false);
                    requestFocusInWindow();
                } else {
                    add((Component) scene.getPanel());
                    ((Component) scene.getPanel()).setFocusable(false);
                    requestFocusInWindow();
                }
            } else {
                if(scenes.keySet().size() > 1) {
                    dialogs.get(i).removeAll();
                    dialogs.get(i++).add((Component) scene.getPanel());
                } else {
                    scene.getPanel().setSize(new Dimension(getWidth(), getHeight()));
                    if(scene.getPanel() instanceof PanelGL) {
                        add(((PanelGL) scene.getPanel()).getGljPanel());
                        ((PanelGL) scene.getPanel()).setFocusable(false);
                    } else {
                        add((Component) scene.getPanel());
                        ((Component) scene.getPanel()).setFocusable(false);
                    }
                }
            }
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