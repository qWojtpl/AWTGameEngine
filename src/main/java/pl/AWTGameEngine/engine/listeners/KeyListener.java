package pl.AWTGameEngine.engine.listeners;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.enums.KeyCode;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyListener implements java.awt.event.KeyListener {

    private final BaseWindow window;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Character> pressedKeysChars = new HashSet<>();

    public KeyListener(BaseWindow window) {
        this.window = window;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        for(Scene scene : window.getScenes()) {
            for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onKeyType#char")) {
                component.onKeyType(e.getKeyChar());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        pressedKeysChars.add(e.getKeyChar());

        for(Scene scene : window.getScenes()) {
            for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onKeyType#int")) {
                component.onKeyType(e.getKeyCode());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        pressedKeysChars.remove(e.getKeyChar());
    }

    public void asKeyPress(int key) {
        pressedKeys.add(key);
    }

    public void asKeyType(int key) {
        for(Scene scene : window.getScenes()) {
            for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onKeyType#int")) {
                component.onKeyType(key);
            }
        }
    }

    public void asKeyType(char character) {
        for(Scene scene : window.getScenes()) {
            for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onKeyType#char")) {
                component.onKeyType(character);
            }
        }
    }

    public void asKeyRelease(int key) {
        releaseKey(key);
    }

    public boolean hasPressedKey(int key) {
        return pressedKeys.contains(key);
    }

    public boolean hasPressedKey(KeyCode key) {
        return hasPressedKey(key.value);
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
