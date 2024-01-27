package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.components.TextArea;
import pl.AWTGameEngine.engine.DialogManager;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

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
    private String currentDirectory = "";

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
        getWindow().getProjectManager().openProject(screenPanel.getParentObject(), "test");
        listFiles(null);
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
        updateCameraPosition();
        moveObjectToMouse();
        loadInfoFields();
        scrollCameraBind.setMaxValue(filesFlex.getCalculatedHeight());
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(object != null) {
            if(!screenPanel.equals(object.getPanel())) {
                return;
            }
        }
        selectObject(object);
    }

    private void updateCameraPosition() {
        if(screenCamera == null || isAnyTextAreaFocused()) {
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
    }

    private void moveObjectToMouse() {
        if(selectedObjectBorder == null) {
            return;
        }
        if(!getMouseListener().isMouseDragged()) {
            return;
        }
        selectedObjectBorder.getObject().setX(getMouseListener().getMouseX());
        selectedObjectBorder.getObject().setY(getMouseListener().getMouseY());
    }

    private void loadInfoFields() {
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
            if(!objectPosY.isFocused()) {
                objectPosY.setText(selectedObjectBorder.getObject().getY() + "");
            } else {
                try {
                    selectedObjectBorder.getObject().setY(Integer.parseInt(objectPosY.getText()));
                } catch(Exception e) {
                    objectPosY.setText("0");
                    selectedObjectBorder.getObject().setY(0);
                }
            }
            if(!objectSizeX.isFocused()) {
                objectSizeX.setText(selectedObjectBorder.getObject().getSizeX() + "");
            } else {
                try {
                    selectedObjectBorder.getObject().setSizeX(Integer.parseInt(objectSizeX.getText()));
                } catch(Exception e) {
                    objectSizeX.setText("0");
                    selectedObjectBorder.getObject().setSizeX(0);
                }
            }
            if(!objectSizeY.isFocused()) {
                objectSizeY.setText(selectedObjectBorder.getObject().getSizeY() + "");
            } else {
                try {
                    selectedObjectBorder.getObject().setSizeX(Integer.parseInt(objectSizeY.getText()));
                } catch(Exception e) {
                    objectSizeY.setText("0");
                    selectedObjectBorder.getObject().setSizeY(0);
                }
            }
        }
    }

    private boolean isAnyTextAreaFocused() {
        return  objectPosX.isFocused() ||
                objectPosY.isFocused() ||
                objectSizeX.isFocused() ||
                objectSizeY.isFocused();
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

    public void listFiles(String subDirectory) {
        if(subDirectory != null) {
            currentDirectory = subDirectory + "/";
        }
        GameObject filesFlex = getScene().getGameObjectByName("filesFlex");
        List<GameObject> children = new ArrayList<>(filesFlex.getAllChildren());
        for(GameObject child : children) {
            getScene().removeGameObject(child);
        }
        List<File> files = getWindow().getProjectManager().getProjectFiles(currentDirectory);
        for(File file : files) {
            createFileObject(filesFlex, file);
        }
    }

    private void createFileObject(GameObject parent, File file) {
        GameObject fileObject = getScene().createGameObject("@file-" + file.getName() + "-" + System.nanoTime());
        fileObject.setSize(96, 96);
        FileComponent fileComponent = new FileComponent(fileObject, file, this);
        fileComponent.setText(getFileName(file.getName()));
        Sprite sprite;
        if(file.isDirectory()) {
            sprite = ResourceManager.getResourceAsSprite("sprites/base/files/directory.png");
        } else {
            sprite = ResourceManager.getResourceAsSprite("sprites/base/files/file.png");
        }
        fileComponent.setSprite(sprite);
        fileObject.addComponent(fileComponent);
        fileObject.setParent(parent);
    }

    public void openFile(FileComponent component) {
        if(component.getFile().isDirectory()) {
            listFiles(currentDirectory + component.getFile().getName());
            return;
        }
        new Thread(() -> {
            try {
                Desktop.getDesktop().open(component.getFile());
            } catch(IOException e) {
                Logger.log("Cannot open file: " + component.getFile().getName(), e);
                DialogManager.createError("Cannot open this file!");
            }
        }).start();
    }

    private String getFileName(String fileName) {
        if(fileName.length() <= 16) {
            return fileName;
        }
        return fileName.substring(0, 13) + "...";
    }

    public GameObject getSelectedObject() {
        if(selectedObjectBorder == null) {
            return null;
        }
        return selectedObjectBorder.getObject();
    }

}