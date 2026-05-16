package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;

import java.awt.*;

public interface PanelObject {

    BaseWindow getWindow();
    Camera getCamera();
    Scene getParentScene();
    Component add(Component comp);
    void unload();
    void updateRender();
    void setSize(Dimension dimension);
    void setPreferredSize(Dimension dimension);
    Dimension getSize();
    void setCursor(Cursor cursor);
    void setOpaque(boolean opaque);
    void printToGraphics(Graphics2D g);
    void onSceneLoad();

}
