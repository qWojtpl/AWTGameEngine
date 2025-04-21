package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;

public interface PanelObject {

    Window getWindow();
    Camera getCamera();
    MouseListener getMouseListener();
    Component add(Component comp);
    void unload();
    void updateRender();
    void updateScene();
    void setSize(Dimension dimension);
    void setPreferredSize(Dimension dimension);
    void setMouseListener(MouseListener listener);
    Dimension getSize();

}
