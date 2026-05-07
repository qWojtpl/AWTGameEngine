package pl.AWTGameEngine.windows;

import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.listeners.WindowListener;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

import java.awt.*;
import java.util.List;

public interface BaseWindow {

    String getTitle();
    void setTitle(String title);
    void setIconImage(Image image);
    boolean isFocused();
    int getWidth();
    int getHeight();
    //
    void moveMouseTo(double x, double y);
    void moveMouseToCenter();
    double[] getScreenCenter();
    void close();
    void unloadScenes();
    void init();
    void updateRatio(int r1, int r2);
    boolean isSameSize();
    List<PanelObject> getPanels();
    BaseLoop getRenderLoop();
    BaseLoop getUpdateLoop();
    BaseLoop getPhysicsLoop();
    BaseLoop getNetLoop();
    MouseListener getMouseListener();
    KeyListener getKeyListener();
    WindowListener getWindowListener();
    Scene getCurrentScene();
    SceneLoader getSceneLoader();
    List<Scene> getScenes();
    Font getFont();
    Font getFont(float size);
    boolean isFullScreen();
    int getBaseWidth();
    int getBaseHeight();
    Cursor getCursor();
    List<Dialog> getDialogs();
    void setRenderLoop(BaseLoop loop);
    void setUpdateLoop(BaseLoop loop);
    void setPhysicsLoop(BaseLoop loop);
    void setNetLoop(BaseLoop loop);
    void setMouseListener(MouseListener listener);
    void setKeyListener(KeyListener listener);
    void setWindowListener(WindowListener windowListener);
    void setSameSize(boolean sameSize);
    void addScene(Scene scene);
    void updateDialogs();
    void setVisible(boolean visible);
    void setCurrentScene(Scene newCurrentScene);
    void setSceneLoader(SceneLoader sceneLoader);
    void setFullScreen(boolean fullScreen);
    void setCursor(Cursor cursor);

}
