package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

@Unique
public class Editor extends ObjectComponent {

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

    @Override
    public void onContextMenuClick(int option, int x, int y) {
        switch(option) {
            case 0:
                String identifier = (String) JOptionPane.showInputDialog(getWindow(),
                        "Provide new object name",
                        "New GameObject",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "GameObject");
                if(identifier != null) {
                    if(getScene().getGameObjectByName(identifier) != null) {
                        JOptionPane.showMessageDialog(getWindow(),
                                "GameObject with this identifier already exists.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
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
