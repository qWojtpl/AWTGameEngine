package pl.AWTGameEngine.engine.listeners;


import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.event.MouseEvent;

public class MouseListener implements java.awt.event.MouseListener {

    private final Window window;
    private int mouseX;
    private int mouseY;
    private MouseEvent clickEvent;
    private MouseEvent pressEvent;
    private MouseEvent releaseEvent;
    private MouseEvent enterEvent;
    private MouseEvent exitEvent;

    public MouseListener(Window window) {
        this.window = window;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clickEvent = e;
        updateMouse(e);
/*        for(GameObject object : window.getCurrentScene().getActiveGameObjects()) {
            for(ObjectComponent component : object.getComponents()) {
                component.onMouseTrigger();
            }
        }*/
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressEvent = e;
        updateMouse(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releaseEvent = e;
        updateMouse(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        enterEvent = e;
        updateMouse(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        exitEvent = e;
        updateMouse(e);
    }

    public void refresh() {
        clickEvent = null;
        pressEvent = null;
        releaseEvent = null;
        enterEvent = null;
        exitEvent = null;
    }

    public void updateMouse(MouseEvent e) {
        Camera camera = window.getCurrentScene().getCamera();
        mouseX = (int) (e.getX() / camera.getZoom() + camera.getX());
        mouseY = (int) (e.getY() / camera.getZoom() + camera.getY());
    }

    public Window getWindow() {
        return this.window;
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }

    public MouseEvent getClickEvent() {
        return this.clickEvent;
    }

    public MouseEvent getPressEvent() {
        return this.pressEvent;
    }

    public MouseEvent getReleaseEvent() {
        return this.releaseEvent;
    }

    public MouseEvent getEnterEvent() {
        return this.enterEvent;
    }

    public MouseEvent getExitEvent() {
        return this.exitEvent;
    }

    public boolean isMouseClicked() {
        return getClickEvent() != null;
    }

    public boolean isMousePressed() {
        return getPressEvent() != null;
    }

    public boolean isMouseReleased() {
        return getReleaseEvent() != null;
    }

    public boolean isMouseEntered() {
        return getEnterEvent() != null;
    }

    public boolean isMouseExited() {
        return getExitEvent() != null;
    }

}
