package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.components.TextArea;
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
    private TextArea objectNameText;
    private TextArea objectPosX;
    private TextArea objectPosY;
    private TextArea objectSizeX;
    private TextArea objectSizeY;

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
        objectNameText = (TextArea) getComponent("objectInfoName", TextArea.class);
        objectNameText.getTextRenderer().align(TextRenderer.HorizontalAlign.RIGHT);
        objectNameText.getTextRenderer().align(TextRenderer.VerticalAlign.CENTER);
        objectNameText.getTextRenderer().setSize(18);
        objectPosX = (TextArea) getComponent("objectInfoPosX", TextArea.class);
        objectPosX.getTextRenderer().align(TextRenderer.HorizontalAlign.RIGHT);
        objectPosX.getTextRenderer().align(TextRenderer.VerticalAlign.CENTER);
        objectPosX.getTextRenderer().setSize(14);
        objectPosY = (TextArea) getComponent("objectInfoPosY", TextArea.class);
        objectPosY.getTextRenderer().align(TextRenderer.HorizontalAlign.RIGHT);
        objectPosY.getTextRenderer().align(TextRenderer.VerticalAlign.CENTER);
        objectPosY.getTextRenderer().setSize(14);
        objectSizeX = (TextArea) getComponent("objectInfoSizeX", TextArea.class);
        objectSizeX.getTextRenderer().align(TextRenderer.HorizontalAlign.RIGHT);
        objectSizeX.getTextRenderer().align(TextRenderer.VerticalAlign.CENTER);
        objectSizeX.getTextRenderer().setSize(14);
        objectSizeY = (TextArea) getComponent("objectInfoSizeY", TextArea.class);
        objectSizeY.getTextRenderer().align(TextRenderer.HorizontalAlign.RIGHT);
        objectSizeY.getTextRenderer().align(TextRenderer.VerticalAlign.CENTER);
        objectSizeY.getTextRenderer().setSize(14);
    }

    private ObjectComponent getComponent(String identifier, Class<? extends ObjectComponent> clazz) {
        GameObject object = getScene().getGameObjectByName(identifier);
        if(object == null) {
            Logger.log(1, "Can't find " + identifier + " object, editor cannot be initialized!");
            System.exit(1);
            return null;
        }
        ObjectComponent objectComponent = object.getComponentByClass(clazz);
        if(objectComponent == null) {
            Logger.log(1, identifier + " object doesn't contains " + clazz.getSimpleName() +
                    ", editor cannot be initialized!");
            System.exit(1);
            return null;
        }
        return objectComponent;
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
        if(selectedObjectBorder != null) {
            if(!objectPosX.isFocused()) {
                objectPosX.setText(selectedObjectBorder.getObject().getX() + "");
            } else {
                try {
                    selectedObjectBorder.getObject().setX(Integer.parseInt(objectPosX.getText()));
                } catch(Exception e) {
                    objectPosX.setText("0");
                    selectedObjectBorder.getObject().setX(0);
                }
            }
        }
        scrollCameraBind.setMaxValue(filesFlex.getCalculatedHeight());
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(object != null) {
            if (!screenPanel.equals(object.getPanel())) {
                return;
            }
        }
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