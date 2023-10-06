package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

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
