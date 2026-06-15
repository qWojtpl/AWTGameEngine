package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.exceptions.scenes.SceneOwnershipException;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Headless window is provided for server builds and points
 * to reduce of RAM usage. Variables are reduced to absolute minimum.
 */
public class HeadlessWindow implements BaseWindow {

    private BaseLoop updateLoop;
    private BaseLoop renderLoop;
    private BaseLoop physicsLoop;
    private BaseLoop netLoop;
    private SceneLoader sceneLoader;
    private final HashMap<Scene, Boolean> scenes = new HashMap<>();

    // empty listeners
    private MouseListener mouseListener;
    private WindowListener windowListener;
    private KeyListener keyListener;

    @Override
    public String getTitle() {
        return "Headless Window";
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setIconImage(Image image) {

    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setResizable(boolean resizable) {

    }

    @Override
    public Dimension getSize() {
        return new Dimension(0, 0);
    }

    @Override
    public void setSize(int x, int y) {

    }

    @Override
    public Point getLocation() {
        return new Point(0, 0);
    }

    @Override
    public void moveMouseTo(double x, double y) {

    }

    @Override
    public void moveMouseToCenter() {

    }

    @Override
    public double[] getScreenCenter() {
        return new double[]{0, 0};
    }

    @Override
    public void close() {
        windowListener.windowClosing(null);
    }

    @Override
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

    @Override
    public void init() {
        setMouseListener(new MouseListener(this));
        setKeyListener(new KeyListener(this));
        setWindowListener(new WindowListener(this));
    }

    @Override
    public void setRatio(int r1, int r2) {

    }

    @Override
    public void updateRatio() {

    }

    @Override
    public boolean isSameSize() {
        return false;
    }

    @Override
    public List<PanelObject> getPanels() {
        return List.of();
    }

    @Override
    public BaseLoop getRenderLoop() {
        return this.renderLoop;
    }

    @Override
    public BaseLoop getUpdateLoop() {
        return this.updateLoop;
    }

    @Override
    public BaseLoop getPhysicsLoop() {
        return this.physicsLoop;
    }

    @Override
    public BaseLoop getNetLoop() {
        return this.netLoop;
    }

    @Override
    public MouseListener getMouseListener() {
        return this.mouseListener;
    }

    @Override
    public KeyListener getKeyListener() {
        return this.keyListener;
    }

    @Override
    public WindowListener getWindowListener() {
        return this.windowListener;
    }

    @Override
    public Scene getCurrentScene() {
        for(Scene scene : scenes.keySet()) {
            if(scenes.get(scene)) {
                return scene;
            }
        }
        return null;
    }

    @Override
    public SceneLoader getSceneLoader() {
        return this.sceneLoader;
    }

    @Override
    public List<Scene> getScenes() {
        return new ArrayList<>(scenes.keySet());
    }

    @Override
    public Font getFont() {
        return null;
    }

    @Override
    public Font getFont(float size) {
        return null;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public int getBaseWidth() {
        return 0;
    }

    @Override
    public int getBaseHeight() {
        return 0;
    }

    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public List<Dialog> getDialogs() {
        return List.of();
    }

    @Override
    public void setRenderLoop(BaseLoop loop) {
        this.renderLoop = loop;
    }

    @Override
    public void setUpdateLoop(BaseLoop loop) {
        this.updateLoop = loop;
    }

    @Override
    public void setPhysicsLoop(BaseLoop loop) {
        this.physicsLoop = loop;
    }

    @Override
    public void setNetLoop(BaseLoop loop) {
        this.netLoop = loop;
    }

    @Override
    public void setMouseListener(MouseListener listener) {
        this.mouseListener = listener;
    }

    @Override
    public void setKeyListener(KeyListener listener) {
        this.keyListener = listener;
    }

    @Override
    public void setWindowListener(WindowListener windowListener) {
        this.windowListener = windowListener;
    }

    @Override
    public void setSameSize(boolean sameSize) {

    }

    @Override
    public void addScene(Scene scene) {
        scenes.putIfAbsent(scene, scenes.isEmpty());
    }

    @Override
    public void updateDialogs() {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public void setCurrentScene(Scene newCurrentScene) {
        if(scenes.getOrDefault(newCurrentScene, null) == null) {
            throw new SceneOwnershipException();
        }
        List<Scene> sceneList = new ArrayList<>(scenes.keySet());
        for(Scene scene : sceneList) {
            scenes.replace(scene, scene.equals(newCurrentScene));
        }
    }

    @Override
    public void setSceneLoader(SceneLoader sceneLoader) {
        this.sceneLoader = sceneLoader;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {

    }

    @Override
    public void setCursor(Cursor cursor) {

    }

}
