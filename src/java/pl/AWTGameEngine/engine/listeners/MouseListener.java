package pl.AWTGameEngine.engine.listeners;

import java.awt.event.MouseEvent;

public class MouseListener implements java.awt.event.MouseListener {

    private MouseEvent releaseEvent;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releaseEvent = e;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void refresh() {
        releaseEvent = null;
    }

    public boolean isMouseReleased() {
        return this.releaseEvent != null;
    }

    public MouseEvent getReleaseEvent() {
        return this.releaseEvent;
    }

}
