package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

//todo: mouselistener for web
public class MouseListener implements
        java.awt.event.MouseListener,
        java.awt.event.MouseMotionListener,
        java.awt.event.MouseWheelListener {

    private final PanelObject panel;
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

    public MouseListener(PanelObject panel) {
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clickEvent = e;
        GameObject clickedObject = null;
        for(GameObject object : getPanel().getWindow().getCurrentScene().getActiveGameObjects()) {
            if(!panel.equals(object.getPanel())) {
                continue;
            }
            if(getMouseX() >= object.getX() && getMouseX() <= object.getX() + object.getSizeX()
            && getMouseY() >= object.getY() && getMouseY() <= object.getY() + object.getSizeY()) {
                for(ObjectComponent component : object.getEventHandler().getComponents("onMouseClick")) {
                    component.onMouseClick();
                }
                clickedObject = object;
            }
        }
        for(ObjectComponent component : panel.getWindow().getCurrentScene().getSceneEventHandler().getComponents("onMouseClick#GameObject")) {
            component.onMouseClick(clickedObject);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressEvent = e;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releaseEvent = e;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        enterEvent = e;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        exitEvent = e;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dragEvent = e;
        updatePosition(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updatePosition(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelEvent = e;
    }

    public void updatePosition(MouseEvent e) {
        Camera camera = panel.getCamera();
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

    public PanelObject getPanel() {
        return this.panel;
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
