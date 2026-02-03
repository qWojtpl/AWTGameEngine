package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

public class WindowListener extends ComponentAdapter implements java.awt.event.WindowListener {

    private final Window window;
    private boolean opened = true;
    private boolean iconified = false;
    private boolean activated = true;

    public WindowListener(Window window) {
        this.window = window;
    }

    private void removeWindow() {
        opened = false;
        WindowsManager windowsManager = Dependencies.getWindowsManager();
        windowsManager.removeWindow(window);
        if(window.equals(windowsManager.getDefaultWindow())) {
            window.unloadScenes();
            PhysXManager.getInstance().cleanup();
            Logger.info("Stopped app.");
        }
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
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {
        opened = true;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(window.getCurrentScene() != null) {
            for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onWindowClosing")) {
                component.onWindowClosing();
            }
        }
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
