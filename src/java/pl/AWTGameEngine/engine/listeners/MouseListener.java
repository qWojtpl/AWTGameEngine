package pl.AWTGameEngine.engine.listeners;

import java.awt.event.MouseEvent;

public class MouseListener implements java.awt.event.MouseListener {

    private boolean mouseReleased = false;
    private MouseEvent releaseEvent;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseReleased = true;
        releaseEvent = e;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void refresh() {
        mouseReleased = false;
        releaseEvent = null;
    }

    public boolean isMouseReleased() {
        return this.mouseReleased;
    }

    public MouseEvent getReleaseEvent() {
        return this.releaseEvent;
    }

}
