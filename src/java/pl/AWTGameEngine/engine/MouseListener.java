package pl.AWTGameEngine.engine;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class MouseListener implements java.awt.event.MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Click Me!");
            menu.add(item);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
