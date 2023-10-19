package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.engine.DialogManager;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

@Unique
public class Editor extends ObjectComponent {

    private GameObject selectedObject;
    private boolean cancelClick = false;
    private final HashMap<GameObject, Border> borderComponents = new HashMap<>();

    public Editor(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setStaticMode(true);
        getCamera().setX(getCamera().getX() - 175);
    }

    @Override
    public void onStaticUpdate() {
        if(getKeyListener().hasPressedKey(37)) {
            getCamera().setX(getCamera().getX() - 8);
        }
        if(getKeyListener().hasPressedKey(39)) {
            getCamera().setX(getCamera().getX() + 8);
        }
        if(getKeyListener().hasPressedKey(38)) {
            getCamera().setY(getCamera().getY() - 8);
        }
        if(getKeyListener().hasPressedKey(40)) {
            getCamera().setY(getCamera().getY() + 8);
        }
        if(getMouseListener().isMouseDragged() && selectedObject != null) {
            selectedObject.setX(getMouseListener().getMouseX());
            selectedObject.setY(getMouseListener().getMouseY());
        }
    }

    @Override
    public void onCreateGameObject(GameObject newObject) {
        GameObject treeObject = getScene().getGameObjectByName("@objectTree");
        List<ObjectComponent> components = treeObject.getComponentsByClass(Tree.class);
        if(components.size() == 0) {
            return;
        }
        Tree tree = ((Tree) components.get(0));
        DefaultMutableTreeNode root = tree.getElement("root");
        root.removeAllChildren();
        for(GameObject go : getScene().getGameObjects()) {
            if(go.getIdentifier().startsWith("@")) {
                continue;
            }
            DefaultMutableTreeNode newElement = tree.addElement(go.getIdentifier());
            GameObject parent = go.getParent();
            if(parent == null) {
                tree.addElementTo(newElement, root);
            } else {
                if(parent.getIdentifier().startsWith("@")) {
                    continue;
                }
                tree.addElementTo(newElement, tree.getElement(parent.getIdentifier()));
            }
        }
        tree.reload();
    }

    @Override
    public void onUpdateGameObject(GameObject updatedObject) {

    }

    @Override
    public void onContextMenuOpen(int x, int y) {
        System.out.println(x + " " + y);
    }

    private void removeHighlight(GameObject object) {
        if(object.getParent() != null) {
            removeHighlight(object.getParent());
            return;
        }
        List<GameObject> children = object.getAllChildren();
        children.add(object);
        for(GameObject all : children) {
            if(!borderComponents.containsKey(all)) {
                continue;
            }
            all.removeComponent(borderComponents.get(all));
        }
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(cancelClick) {
            cancelClick = false;
            return;
        }
        selectObject(object);
    }

    @Override
    public void onTreeValueChange(GameObject object, String[] path) {
        if(!object.getIdentifier().equals("@objectTree")) {
            return;
        }
        selectObject(getScene().getGameObjectByName(path[path.length - 1]));
        cancelClick = true;
    }

    public void selectObject(GameObject object) {
        Tree tree = (Tree) getScene().getGameObjectByName("@objectTree").getComponentsByClass(Tree.class).get(0);
        if(selectedObject != null) {
            removeHighlight(selectedObject);
        }
        if(object == null) {
            tree.setSelection(null);
            selectedObject = null;
            return;
        }
        if(object.getIdentifier().startsWith("@")) {
            return;
        }
        selectedObject = object;
        if(object.getParent() != null) {
            selectObject(object.getParent());
            return;
        }
        List<GameObject> children = object.getAllChildren();
        children.add(object);
        for(GameObject all : children) {
            Border borderComponent = new Border(all);
            borderComponent.setColor(Color.RED);
            all.addComponent(borderComponent);
            borderComponents.put(all, borderComponent);
        }
        tree.setSelection(object.getIdentifier());
        cancelClick = false;
    }

    @Override
    public void onContextMenuClick(int option, int x, int y) {
        switch(option) {
            case 0:
                String identifier = DialogManager.createInput(
                        getWindow(),
                        "New GameObject",
                        "Provide new object name",
                        "GameObject");
                if(identifier != null) {
                    if(getScene().getGameObjectByName(identifier) != null) {
                        DialogManager.createError(getWindow(), "Error", "GameObject with this identifier already exists.");
                    } else {
                        GameObject go = getScene().createGameObject(identifier);
                        go.setX(x);
                        go.setY(y);
                        System.out.println(x + " " + y);
                        SpriteRenderer spriteRenderer = new SpriteRenderer(go);
                        spriteRenderer.setImage(ResourceManager.getResourceAsImage("beaver.jpg"));
                        go.addComponent(spriteRenderer);
                        System.out.println(go.getX() + " " + go.getY());
                    }
                }
                break;
        }
    }

}
