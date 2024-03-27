package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.components.TextArea;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.panels.NestedPanel;
import pl.AWTGameEngine.objects.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Random;

@Unique
public class Editor extends ObjectComponent {

    private NestedPanel screenPanel;
    private Camera screenCamera;
    private Border selectedObjectBorder;
    private FlexComponent filesFlex;
    private FlexComponent componentsFlex;
    private ScrollCameraBind scrollCameraBind;
    private ScrollCameraBind objectInfoScroll;
    private TextArea objectNameText;
    private TextArea objectPosX;
    private TextArea objectPosY;
    private TextArea objectSizeX;
    private TextArea objectSizeY;
    private String currentDirectory = "";
    private final List<GameObject> componentInfoList = new ArrayList<>();
    private final List<BindableProperty> bindableProperties = new ArrayList<>();

    public Editor(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setStaticMode(true);
        screenPanel = ((PanelComponent) getComponent("panel", PanelComponent.class)).getNestedPanel();
        screenCamera = screenPanel.getCamera();
        filesFlex = (FlexComponent) getComponent("filesFlex", FlexComponent.class);
        componentsFlex = (FlexComponent) getComponent("componentsFlex", FlexComponent.class);
        scrollCameraBind = (ScrollCameraBind) getComponent("filesScroll", ScrollCameraBind.class);
        objectInfoScroll = (ScrollCameraBind) getComponent("objectInfoScroll", ScrollCameraBind.class);
        objectNameText = initTextArea("objectInfoName", 18);
        objectPosX = initTextArea("objectInfoPosX", 14);
        objectPosY = initTextArea("objectInfoPosY", 14);
        objectSizeX = initTextArea("objectInfoSizeX", 14);
        objectSizeY = initTextArea("objectInfoSizeY", 14);
        getWindow().getProjectManager().createProject("project");
        getWindow().getProjectManager().openProject(screenPanel.getParentObject(), "project");
        //getWindow().getProjectManager().compileProject();
        listFiles(null);
        new BindableProperty(this, filesFlex, "calculatedHeight", scrollCameraBind, "maxValue");
        new BindableProperty(this, componentsFlex, "calculatedHeight", objectInfoScroll, "maxValue");
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
        objectInfoScroll.setWheelSpeed(0.1 / componentsFlex.getObject().getChildren().size());
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

    private TextArea initTextArea(String identifier, int size) {
        TextArea textArea = (TextArea) getComponent(identifier, TextArea.class);
        textArea.getTextRenderer().align(TextRenderer.HorizontalAlign.RIGHT);
        textArea.getTextRenderer().align(TextRenderer.VerticalAlign.CENTER);
        textArea.getTextRenderer().setSize(size);
        return textArea;
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
        MouseListener mouseListener = selectedObjectBorder.getObject().getPanel().getMouseListener();
        if(!mouseListener.isMouseDragged()) {
            return;
        }
        selectedObjectBorder.getObject().setX(mouseListener.getMouseX());
        selectedObjectBorder.getObject().setY(mouseListener.getMouseY());
    }

    private boolean isAnyTextAreaFocused() {
        return  objectPosX.isFocused() ||
                objectPosY.isFocused() ||
                objectSizeX.isFocused() ||
                objectSizeY.isFocused();
    }

    private void selectObject(GameObject object) {
        removeBindings();
        if(selectedObjectBorder != null) {
            selectedObjectBorder.getObject().removeComponent(selectedObjectBorder);
            selectedObjectBorder = null;
        }
        for(GameObject componentObject : componentInfoList) {
            getScene().removeGameObject(componentObject);
        }
        componentInfoList.clear();
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
        for(ObjectComponent component : object.getComponents()) {
            GameObject componentGameObject = getScene().createGameObject("@componentObject-" + System.nanoTime());
            componentGameObject.setX(10);
            componentGameObject.setY(210);
            componentGameObject.setSize(290, new Random().nextInt(400) + 100);
            InfoComponent infoComponent = new InfoComponent(componentGameObject, component);
            componentGameObject.addComponent(infoComponent);
            componentInfoList.add(componentGameObject);
            componentGameObject.setParent(componentsFlex.getObject());
        }
        object.addComponent(selectedObjectBorder);
        registerBindings();
    }

    private void registerBindings() {
        GameObject selectedObject = getSelectedObject();
        if(selectedObject == null) {
            return;
        }
        bindableProperties.clear();
        // Firstly you need to put position to TextArea
        // when TextArea is focused, then bindings locks,
        // so putting text to method should be at second position
        bindableProperties.add(new BindableProperty(this, selectedObject, "x", objectPosX, "text"));
        bindableProperties.add(new BindableProperty(this, objectPosX, "text", selectedObject, "x"));
        bindableProperties.add(new BindableProperty(this, selectedObject, "y", objectPosY, "text"));
        bindableProperties.add(new BindableProperty(this, objectPosY, "text", selectedObject, "y"));
        bindableProperties.add(new BindableProperty(this, selectedObject, "sizeX", objectSizeX, "text"));
        bindableProperties.add(new BindableProperty(this, objectSizeX, "text", selectedObject, "sizeX"));
        bindableProperties.add(new BindableProperty(this, selectedObject, "sizeY", objectSizeY, "text"));
        bindableProperties.add(new BindableProperty(this, objectSizeY, "text", selectedObject, "sizeY"));
    }

    private void removeBindings() {
        for(BindableProperty bindableProperty : bindableProperties) {
            getBindingsManager().removeBindableProperty(bindableProperty);
        }
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
            if(file.getName().startsWith("_")) {
                continue;
            }
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
            String after = "";
            switch(getFileExtension(file.getName())) {
                case "scene":
                    after = "_scene";
                    break;
                case "png":
                case "jpg":
                    after = "_img";
            }
            sprite = ResourceManager.getResourceAsSprite("sprites/base/files/file" + after + ".png");
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
        if(getFileExtension(component.getFile().getName()).equals("scene")) {
            loadScene(component.getFile().getPath());
            return;
        }
        new Thread(() -> {
            try {
                Desktop.getDesktop().open(component.getFile());
            } catch(IOException e) {
                Logger.log("Cannot open file: " + component.getFile().getName(), e);
            }
        }).start();
    }

    private void loadScene(String path) {
        path = path.replace("\\", "/");
        List<GameObject> objectsToRemove = new ArrayList<>();
        for(GameObject object : getScene().getGameObjects()) {
            if(object.getIdentifier().equals("@editor")) {
                continue;
            }
            if(screenPanel.equals(object.getPanel())) {
                objectsToRemove.add(object);
            }
        }
        for(GameObject object : new ArrayList<>(objectsToRemove)) {
            getScene().removeGameObject(object);
        }
        //getSceneLoader().attachSceneData(getSceneLoader().getSceneData(path), screenPanel.getParentObject());
    }

    private String getFileName(String fileName) {
        if(fileName.length() <= 16) {
            return fileName;
        }
        return fileName.substring(0, 13) + "...";
    }

    private String getFileExtension(String fileName) {
        String[] split = fileName.split("\\.");
        if(split.length == 0) {
            return fileName;
        }
        return split[split.length - 1];
    }

    public GameObject getSelectedObject() {
        if(selectedObjectBorder == null) {
            return null;
        }
        return selectedObjectBorder.getObject();
    }

}