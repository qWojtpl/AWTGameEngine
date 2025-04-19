package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
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
            Dependencies.getResourceManager().clearAudioClips();
            Logger.log(2, "Stopped app.");
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
        if(!window.isSameSize()) {
            return;
        }
        window.getPanel().setPreferredSize(new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight()));
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
