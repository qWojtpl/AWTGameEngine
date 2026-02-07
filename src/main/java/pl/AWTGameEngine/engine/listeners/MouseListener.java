package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseListener {

    private final Window window;
    private int mouseX;
    private int mouseY;
    private int mouseWindowX;
    private int mouseWindowY;
    private int mouseScreenX;
    private int mouseScreenY;
    private MouseEvent clickEvent;
    private MouseEvent pressEvent;
    private MouseEvent releaseEvent;
    private MouseEvent enterEvent;
    private MouseEvent exitEvent;
    private MouseEvent dragEvent;
    private MouseEvent moveEvent;
    private MouseWheelEvent mouseWheelEvent;

    private AWTListener awtListener;

    public MouseListener(Window window) {
        this.window = window;
    }

    public void mouseClicked(MouseEvent e) {
        clickEvent = e;
        for(Scene scene : window.getScenes()) {
            if(!RenderEngine.DEFAULT.equals(scene.getRenderEngine()) && !RenderEngine.WEB.equals(scene.getRenderEngine())) {
                continue;
            }
            GameObject clickedObject = null;
            for(GameObject object : scene.getGameObjects()) {
                if (getMouseX() >= object.getX() && getMouseX() <= object.getX() + object.getSizeX()
                        && getMouseY() >= object.getY() && getMouseY() <= object.getY() + object.getSizeY()) {
                    for (ObjectComponent component : object.getEventHandler().getComponents("onMouseClick")) {
                        component.onMouseClick();
                    }
                    clickedObject = object;
                }
            }
            for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onMouseClick#GameObject")) {
                component.onMouseClick(clickedObject);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        pressEvent = e;
    }

    public void mouseReleased(MouseEvent e) {
        releaseEvent = e;
    }

    public void mouseEntered(MouseEvent e) {
        enterEvent = e;
    }

    public void mouseExited(MouseEvent e) {
        exitEvent = e;
    }

    public void mouseDragged(MouseEvent e) {
        dragEvent = e;
        updatePosition(e);
    }

    public void mouseMoved(MouseEvent e) {
        updatePosition(e);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelEvent = e;
    }

    public void updatePosition(MouseEvent e) {
        if(window.getCurrentScene() == null) {
            return;
        }
        Camera camera = window.getCurrentScene().getPanel().getCamera();
        mouseX = (int) (e.getX() / camera.getMultiplier() + camera.getX());
        mouseY = (int) (e.getY() / camera.getMultiplier() + camera.getY());
        mouseWindowX = e.getX();
        mouseWindowY = e.getY();
        mouseScreenX = e.getXOnScreen();
        mouseScreenY = e.getYOnScreen();
        moveEvent = e;
    }

    public void refresh() {
        clickEvent = null;
        pressEvent = null;
        releaseEvent = null;
        enterEvent = null;
        exitEvent = null;
        dragEvent = null;
        moveEvent = null;
        mouseWheelEvent = null;
    }

    public void adaptAWTEvent(AWTEvent event) {
        if(event instanceof MouseWheelEvent e) {
            mouseWheelMoved(e);
            return;
        }

        if(!(event instanceof MouseEvent e)) {
            return;
        }

        switch(e.getID()) {
            case MouseEvent.MOUSE_CLICKED -> mouseClicked(e);
            case MouseEvent.MOUSE_PRESSED -> mousePressed(e);
            case MouseEvent.MOUSE_RELEASED -> mouseReleased(e);
            case MouseEvent.MOUSE_ENTERED -> mouseEntered(e);
            case MouseEvent.MOUSE_EXITED -> mouseExited(e);
            case MouseEvent.MOUSE_DRAGGED -> mouseDragged(e);
            case MouseEvent.MOUSE_MOVED -> mouseMoved(e);
        }
    }

    public AWTListener getAWTListener() {
        return this.awtListener;
    }

    public void setAWTListener(AWTListener listener) {
        if(!this.equals(listener.getTarget())) {
            throw new RuntimeException("AWTListener target is wrong.");
        }
        this.awtListener = listener;
    }

    /**
     * Returns mouse position in window relative to camera.
     */
    public int getMouseX() {
        return this.mouseX;
    }

    /**
     * Returns mouse position in window relative to camera.
     */
    public int getMouseY() {
        return this.mouseY;
    }

    /**
     * Returns mouse position in window. For the top left corner of window it will return 0. Not relative to camera.
     */
    public int getWindowMouseX() {
        return this.mouseWindowX;
    }

    /**
     * Returns mouse position in window. For the top left corner of window it will return 0. Not relative to camera.
     */
    public int getWindowMouseY() {
        return this.mouseWindowY;
    }

    /**
     * Returns mouse position on screen. For the top left corner of screen it will return 0. Not relative to window or camera.
     */
    public int getMouseScreenX() {
        return this.mouseScreenX;
    }

    /**
     * Returns mouse position on screen. For the top left corner of screen it will return 0. Not relative to window or camera.
     */
    public int getMouseScreenY() {
        return this.mouseScreenY;
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

    public MouseEvent getDragEvent() {
        return this.dragEvent;
    }

    public MouseEvent getMoveEvent() {
        return this.moveEvent;
    }

    public MouseWheelEvent getMouseWheelEvent() {
        return this.mouseWheelEvent;
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

    public boolean isMouseDragged() {
        return getDragEvent() != null;
    }

    public boolean isMouseMoved() {
        return getMoveEvent() != null;
    }

    public boolean isMouseWheeled() {
        return getMouseWheelEvent() != null;
    }

}
