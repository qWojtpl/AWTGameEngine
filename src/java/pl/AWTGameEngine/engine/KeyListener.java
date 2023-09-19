package pl.AWTGameEngine.engine;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyListener implements java.awt.event.KeyListener {

    private final static Set<Integer> pressedKeys = new HashSet<>();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    public static boolean hasPressedKey(int key) {
        return pressedKeys.contains(key);
    }

    public static void releaseKey(int key) {
        pressedKeys.remove(key);
    }

}
