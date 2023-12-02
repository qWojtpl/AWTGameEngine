package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;

import java.awt.event.WindowEvent;

public class WindowListener implements java.awt.event.WindowListener {

    private final Window window;
    private boolean opened = true;
    private boolean iconified = false;
    private boolean activated = true;

    public WindowListener(Window window) {
        this.window = window;
    }

    private void removeWindow() {
        opened = false;
        WindowsManager.removeWindow(window);
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
    public void windowOpened(WindowEvent e) {
        opened = true;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(window.getCurrentScene() != null) {
            for(GameObject go : window.getCurrentScene().getActiveGameObjects()) {
                for(ObjectComponent component : go.getComponents()) {
                    component.onWindowClosing();
                }
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
