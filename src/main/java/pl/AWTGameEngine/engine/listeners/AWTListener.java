package pl.AWTGameEngine.engine.listeners;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class AWTListener implements AWTEventListener {

    private final MouseListener target;

    public AWTListener(MouseListener target) {
        this.target = target;
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        if(!(event instanceof MouseEvent)) {
            return;
        }
        target.adaptAWTEvent(event);
    }

    public MouseListener getTarget() {
        return this.target;
    }

}
