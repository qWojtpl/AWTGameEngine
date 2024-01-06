package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Unique
public class Editor extends ObjectComponent {

    private GameObject gameScreen;
    private Camera screenCamera;
    private Border selectedObjectBorder;

    public Editor(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setStaticMode(true);
        gameScreen = getScene().getGameObjectByName("panel");
        if(gameScreen == null) {
            Logger.log(1, "Can't find panel (game screen) object, editor cannot be initialized!");
            System.exit(1);
            return;
        }
        if(gameScreen.getComponentsByClass(PanelComponent.class).size() == 0) {
            Logger.log(1, "Panel (game screen) object doesn't contains PanelComponent, editor cannot be initialized!");
            System.exit(1);
            return;
        }
        screenCamera = ((PanelComponent) gameScreen.getComponentsByClass(PanelComponent.class).get(0)).getPanelCamera();
    }

    @Override
    public void onStaticUpdate() {
        if(gameScreen == null || screenCamera == null) {
            return;
        }
        if(getKeyListener().hasPressedKey(37)) {
            screenCamera.setX(screenCamera.getX() - 8);
            GameObject object = getScene().getGameObjectByName("audio2");
            MusicPlayer player = new MusicPlayer(object);
            object.addComponent(player);
            player.setSourcePath("audio/dominique2.wav");
            player.play();
        }
        if(getKeyListener().hasPressedKey(38)) {
            screenCamera.setY(screenCamera.getY() - 8);
        }
        if(getKeyListener().hasPressedKey(39)) {
            screenCamera.setX(screenCamera.getX() + 8);
        }
        if(getKeyListener().hasPressedKey(40)) {
            screenCamera.setY(screenCamera.getY() + 8);
        }
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(selectedObjectBorder != null) {
            selectedObjectBorder.getObject().removeComponent(selectedObjectBorder);
            selectedObjectBorder = null;
        }
        if(object == null) {
            return;
        }
        if(object.getIdentifier().startsWith("@")) {
            return;
        }
        selectedObjectBorder = new Border(object);
        selectedObjectBorder.setColor(new ColorObject(Color.RED));
        object.addComponent(selectedObjectBorder);
    }

    public GameObject getSelectedObject() {
        if(selectedObjectBorder == null) {
            return null;
        }
        return selectedObjectBorder.getObject();
    }

}