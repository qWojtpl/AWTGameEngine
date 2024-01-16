package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Unique
public class Editor extends ObjectComponent {

    private NestedPanel screenPanel;
    private Camera screenCamera;
    private Border selectedObjectBorder;
    private FlexComponent filesFlex;
    private ScrollCameraBind scrollCameraBind;
    private TextRenderer objectNameText;

    public Editor(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setStaticMode(true);
        screenPanel = ((PanelComponent) getComponent("panel", PanelComponent.class)).getNestedPanel();
        screenCamera = screenPanel.getCamera();
        filesFlex = (FlexComponent) getComponent("filesFlex", FlexComponent.class);
        scrollCameraBind = (ScrollCameraBind) getComponent("filesScroll", ScrollCameraBind.class);
        objectNameText = (TextRenderer) getComponent("objectInfoName", TextRenderer.class);
        objectNameText.align(TextRenderer.HorizontalAlign.LEFT);
        objectNameText.align(TextRenderer.VerticalAlign.CENTER);
    }

    private ObjectComponent getComponent(String identifier, Class<? extends ObjectComponent> component) {
        GameObject object = getScene().getGameObjectByName(identifier);
        if(object == null) {
            Logger.log(1, "Can't find " + identifier + " object, editor cannot be initialized!");
            System.exit(1);
            return null;
        }
        if(object.getComponentsByClass(component).size() == 0) {
            Logger.log(1, identifier + " object doesn't contains " + component.getSimpleName() +
                    ", editor cannot be initialized!");
            System.exit(1);
            return null;
        }
        return object.getComponentsByClass(component).get(0);
    }

    @Override
    public void onStaticUpdate() {
        if(screenCamera == null) {
            return;
        }
        if(getKeyListener().hasPressedKey(37)) {
            screenCamera.setX(screenCamera.getX() - 8);
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
        if(getMouseListener().isMouseDragged()) {
            if(selectedObjectBorder != null) {
                selectedObjectBorder.getObject().setX(getMouseListener().getMouseX());
                selectedObjectBorder.getObject().setY(getMouseListener().getMouseY());
            }
        }
        scrollCameraBind.setMaxValue(filesFlex.getCalculatedHeight());
    }

    @Override
    public void onMouseClick(GameObject object) {
        selectObject(object);
    }

    private void selectObject(GameObject object) {
        if(selectedObjectBorder != null) {
            selectedObjectBorder.getObject().removeComponent(selectedObjectBorder);
            selectedObjectBorder = null;
        }
        if(object == null) {
            return;
        }
        if(!object.getPanel().equals(screenPanel)) {
            return;
        }
        if(object.getIdentifier().startsWith("@")) {
            return;
        }
        selectedObjectBorder = new Border(object);
        selectedObjectBorder.setColor(new ColorObject(Color.RED));
        objectNameText.setText(object.getIdentifier());
        object.addComponent(selectedObjectBorder);

    }

    public GameObject getSelectedObject() {
        if(selectedObjectBorder == null) {
            return null;
        }
        return selectedObjectBorder.getObject();
    }

}