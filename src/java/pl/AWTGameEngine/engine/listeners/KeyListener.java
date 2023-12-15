package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyListener implements java.awt.event.KeyListener {

    private final Window window;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Character> pressedKeysChars = new HashSet<>();

    public KeyListener(Window window) {
        this.window = window;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(window.getCurrentScene() == null) {
            return;
        }
        for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onKeyType")) {
            component.onKeyType(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        pressedKeysChars.add(e.getKeyChar());
        for(ObjectComponent component : window.getCurrentScene().getSceneEventHandler().getComponents("onKeyType")) {
            component.onKeyType(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        pressedKeysChars.remove(e.getKeyChar());
    }

    public boolean hasPressedKey(int key) {
        return pressedKeys.contains(key);
    }

    public void releaseKey(int key) {
        pressedKeys.remove(key);
    }

    public Set<Integer> getPressedKeys() {
        return this.pressedKeys;
    }

    public Set<Character> getPressedKeysChars() {
        return this.pressedKeysChars;
    }

}
