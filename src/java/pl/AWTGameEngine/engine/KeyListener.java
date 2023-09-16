package pl.AWTGameEngine.engine;

import java.awt.event.KeyEvent;

public class KeyListener implements java.awt.event.KeyListener {

    private static int lastType;
    private static int lastPress;
    private static int lastRelease;

    @Override
    public void keyTyped(KeyEvent e) {
        lastType = e.getKeyCode();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        lastPress = e.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        lastRelease = e.getKeyCode();
    }

    public static int getLastType() {
        return lastType;
    }

    public static int getLastPress() {
        return lastPress;
    }

    public static int getLastRelease() {
        return lastRelease;
    }

}
