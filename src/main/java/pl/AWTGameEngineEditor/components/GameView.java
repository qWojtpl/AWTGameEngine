package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngineEditor.manager.EditorManager;

import java.awt.*;

@WebComponent
public class GameView extends ObjectComponent {

    private boolean focusMainWindow = true;

    public GameView(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        EditorManager.getInstance().setGameViewWindow((Window) Dependencies.getWindowsManager().createWindow("scenes/editor/gameview.xml", RenderEngine.DEFAULT, false));
        updateLocation();
    }

    public void loadScene() {

    }

    @Override
    public void onUpdate() {
        boolean shouldBeOnTop = getWindow().getWindowListener().isActivated();
        if(getWindow().getWindowListener().isIconified()) {
            shouldBeOnTop = false;
        }
        Window gameViewWindow = EditorManager.getInstance().getGameViewWindow();
        if(gameViewWindow.isAlwaysOnTop() != shouldBeOnTop) {
            gameViewWindow.setAlwaysOnTop(shouldBeOnTop);
            if(focusMainWindow && shouldBeOnTop) {
                ((Window) getWindow()).requestFocus();
                focusMainWindow = false;
            }
        }
    }

    @Override
    public void onWindowResize(int w, int h) {
        updateLocation();
    }

    @Override
    public void onWindowMove() {
        updateLocation();
    }

    @Override
    public void onMouseClick(int x, int y, int xOnScreen, int yOnScreen) {
        focusMainWindow = true;
    }

    public void updateLocation() {
        Insets windowInsets = ((Window) getWindow()).getInsets();
        double widthMultiplier = (double) getWindow().getWidth() / getWindow().getBaseWidth();
        double heightMultiplier = (double) getWindow().getHeight() / getWindow().getBaseHeight();
        Window gameViewWindow = EditorManager.getInstance().getGameViewWindow();
        gameViewWindow.setSize(
                (int) (1620 * widthMultiplier) - windowInsets.left - windowInsets.right,
                (int) (912 * heightMultiplier) - windowInsets.top - windowInsets.bottom);
        gameViewWindow.setLocation(
                (int) getWindow().getLocation().getX() + windowInsets.left + (int) (300 * widthMultiplier),
                (int) getWindow().getLocation().getY() + windowInsets.top + (int) (80 * heightMultiplier)
        );
    }

    public void setFocusMainWindow(boolean focus) {
        this.focusMainWindow = focus;
    }

}
