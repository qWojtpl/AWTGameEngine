package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;

@WebComponent
public class GameView extends ObjectComponent {

    private Window gameViewWindow;

    public GameView(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        gameViewWindow = (Window) Dependencies.getWindowsManager().createWindow("scenes/net/server.xml", RenderEngine.DEFAULT, false);
        for(Scene scene : gameViewWindow.getScenes()) {
            scene.getSceneEventHandler().clear();
            for(GameObject go : scene.getGameObjects()) {
                go.getEventHandler().clear();
            }
        }
        gameViewWindow.getUpdateLoop().kill();
        gameViewWindow.getPhysicsLoop().kill();
        gameViewWindow.getNetLoop().kill();
        updateLocation();
    }

    @Override
    public void onUpdate() {
        if(gameViewWindow.isFocused() || ((Window) getWindow()).isFocused()) {
            gameViewWindow.toFront();
        } else {
            for(Dialog dialog : gameViewWindow.getDialogs()) {
                if(dialog.isFocused()) {
                    gameViewWindow.toFront();
                    return;
                }
            }
            gameViewWindow.toBack();
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

    public void updateLocation() {
        Insets windowInsets = ((Window) getWindow()).getInsets();
        double widthMultiplier = (double) getWindow().getWidth() / getWindow().getBaseWidth();
        double heightMultiplier = (double) getWindow().getHeight() / getWindow().getBaseHeight();
        gameViewWindow.setSize(
                (int) (1620 * widthMultiplier) - windowInsets.left - windowInsets.right,
                (int) (912 * heightMultiplier) - windowInsets.top - windowInsets.bottom);
        gameViewWindow.setLocation(
                (int) getWindow().getLocation().getX() + windowInsets.left + (int) (300 * widthMultiplier),
                (int) getWindow().getLocation().getY() + windowInsets.top + (int) (80 * heightMultiplier)
        );
    }

}
