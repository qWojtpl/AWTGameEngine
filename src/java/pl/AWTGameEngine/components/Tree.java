package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.GameLoop;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.SceneLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

@Unique
public class Tree extends ObjectComponent {

    private JTree tree;

    public Tree(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        DefaultMutableTreeNode style= new DefaultMutableTreeNode("Style");
        DefaultMutableTreeNode color= new DefaultMutableTreeNode("color");
        DefaultMutableTreeNode font= new DefaultMutableTreeNode("font");
        style.add(color);
        style.add(font);
        tree = new JTree(style);
        tree.setFocusable(false);
        getWindow().getPanel().add(tree);
        tree.setVisible(true);
    }



}
