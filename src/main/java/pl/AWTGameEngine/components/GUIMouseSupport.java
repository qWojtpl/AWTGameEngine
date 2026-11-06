package pl.AWTGameEngine.components;

import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.enums.KeyCode;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.helpers.FXHelper;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.lang.reflect.Method;

/**
 * Component is used to provide support for mouse interactions (click, hover) inside a web GUI.
 */
@WebComponent
public class GUIMouseSupport extends ObjectComponent {

    private WebView webView;
    private WebGraphicsManager webGraphicsManager;
    private Method processMouseEventMethod;

    private final String cursorScript =
            "var el = document.elementFromPoint(%d, %d);" +
            "if(el) {" +
            "    window.getComputedStyle(el).cursor;" +
            "} else {" +
            "    'default';" +
            "}";

    private boolean pressed = false;

    public GUIMouseSupport(GameObject object) {
        super(object);
    }

    @Override
    public void onWebViewInitialization() {
        this.webView = ((WebPanel) getScene().getPanel()).getWebView();
        this.webGraphicsManager = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        if(webView == null) {
            return;
        }
        try {
            processMouseEventMethod = WebView.class.getDeclaredMethod("processMouseEvent", MouseEvent.class);
            processMouseEventMethod.setAccessible(true);
        } catch(Exception e) {
            Logger.exception("Cannot get process mouse event method", e);
        }
    }

    @Override
    public void onUpdate() {
        if(!getWindow().isFocused()) {
            return;
        }
        if(getMouseListener().isMouseMoved() || getMouseListener().isMousePressed() || getMouseListener().isMouseReleased() || getMouseListener().isMouseDragged()) {
            processMouseEvent(
                    getMouseEvent(),
                    getMouseListener().getWindowMouseX(),
                    getMouseListener().getWindowMouseY(),
                    getMouseListener().getClickEvent() == null ? MouseButton.NONE : (
                            switch (getMouseListener().getClickEvent().getButton()) {
                                case 1 -> MouseButton.PRIMARY;
                                case 2 -> MouseButton.SECONDARY;
                                case 3 -> MouseButton.MIDDLE;
                                default -> MouseButton.NONE;
                            }
                    )
            );
        }
    }

    private void processMouseEvent(EventType<MouseEvent> mouseEvent, int x, int y, MouseButton mouseButton) {
        FXHelper.synchronizedCall(() -> {
            try {
                processMouseEventMethod.invoke(webView, new MouseEvent(
                        mouseEvent,
                        x, y,
                        x, y,
                        mouseButton,
                        0,
                        getKeyListener().hasPressedKey(KeyCode.SHIFT),
                        getKeyListener().hasPressedKey(KeyCode.CTRL),
                        getKeyListener().hasPressedKey(KeyCode.ALT),
                        getKeyListener().hasPressedKey(KeyCode.META),
                        mouseButton == MouseButton.PRIMARY,
                        mouseButton == MouseButton.MIDDLE,
                        mouseButton == MouseButton.SECONDARY,
                        false, false, true, null
                ));
                handleCursor(webGraphicsManager.execute(String.format(cursorScript, x, y)));
                if(mouseEvent == MouseEvent.MOUSE_RELEASED) {
                    processMouseEvent(MouseEvent.MOUSE_CLICKED, x, y, mouseButton);
                }
            } catch(Exception e) {
                Logger.exception("Cannot process mouse event", e);
            }
            return true;
        });
    }

    private void handleCursor(String cursor) {
        int c = switch (cursor) {
            case "pointer" -> Cursor.HAND_CURSOR;
            case "text" -> Cursor.TEXT_CURSOR;
            case "move" -> Cursor.MOVE_CURSOR;
            case "wait", "progress" -> Cursor.WAIT_CURSOR;
            case "not-allowed", "crosshair" -> Cursor.CROSSHAIR_CURSOR;
            default -> Cursor.DEFAULT_CURSOR;
        };
        getWindow().setCursor(Cursor.getPredefinedCursor(c));
    }

    private EventType<MouseEvent> getMouseEvent() {
        if(getMouseListener().isMouseMoved()) {
            if(pressed) {
                return MouseEvent.MOUSE_DRAGGED;
            }
            return MouseEvent.MOUSE_MOVED;
        } else if(getMouseListener().isMousePressed()) {
            pressed = true;
            return MouseEvent.MOUSE_PRESSED;
        } else if(getMouseListener().isMouseReleased()) {
            pressed = false;
            return MouseEvent.MOUSE_RELEASED;
        } else {
            return null;
        }
    }

}
