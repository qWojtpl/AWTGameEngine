package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.windows.BaseWindow;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

public class WindowListener extends ComponentAdapter implements java.awt.event.WindowListener {

    private final BaseWindow window;
    private boolean opened = true;
    private boolean iconified = false;
    private boolean activated = true;

    public WindowListener(BaseWindow window) {
        this.window = window;
    }

    private void removeWindow() {
        opened = false;
        Dependencies.getWindowsManager().close(window);
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isIconified() {
        return iconified;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int newWidth = e.getComponent().getWidth();
        int newHeight = e.getComponent().getHeight();
        if(window.isSameSize()) {
            window.updateRatio(16, 9);
        } else {
            for(PanelObject panel : window.getPanels()) {
                panel.setSize(new Dimension(newWidth, newHeight));
            }
        }
        if(window.getCurrentScene() != null) {
            for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onWindowResize#int#int")) {
                component.onWindowResize(newWidth, newHeight);
            }
        }
        window.updateDialogs();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        window.updateDialogs();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        opened = true;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        removeWindow();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        removeWindow();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        iconified = true;
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        iconified = false;
    }

    @Override
    public void windowActivated(WindowEvent e) {
        activated = true;
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        activated = false;
    }

}
