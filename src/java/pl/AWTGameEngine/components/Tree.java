package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.GamePanel;
import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.HashMap;

@Unique
public class Tree extends ObjectComponent {

    private JTree tree;
    private NestedPanel container;
    private HashMap<String, DefaultMutableTreeNode> elements = new HashMap<>();

    public Tree(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Objects");
        DefaultMutableTreeNode color=new DefaultMutableTreeNode("color");
        DefaultMutableTreeNode font=new DefaultMutableTreeNode("font");
        root.add(color);
        root.add(font);
        DefaultMutableTreeNode red=new DefaultMutableTreeNode("red");
        DefaultMutableTreeNode blue=new DefaultMutableTreeNode("blue");
        DefaultMutableTreeNode black=new DefaultMutableTreeNode("black");
        DefaultMutableTreeNode green=new DefaultMutableTreeNode("green");
        color.add(red); color.add(blue); color.add(black); color.add(green);
        tree = new JTree(root);
        tree.setBorder(BorderFactory.createEmptyBorder(8, 16, 1, 1));
        tree.setFocusable(false);
        tree.setFont(getWindow().getFont().deriveFont(18f));
        container = new NestedPanel(getObject());
        container.setBackground(Color.WHITE);
        container.setSize(new Dimension(100, 1000));
        container.setLayout(new BorderLayout());
        container.add(tree);
        getObject().getPanel().add(container);
    }

    @Override
    public void onRender(Graphics g) {
        System.out.println((int) (getObject().getScaleX() * getCamera().getZoom()));
        if(container == null) {
            return;
        }
/*        container.setLocation(
                (int) ((getObject().getX() - getObject().getScaleX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        container.setSize(
                (int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));*/
    }

    @Override
    public void onParentChange(GameObject oldParent) {
        oldParent.getPanel().remove(container);
        getObject().getPanel().add(container);
    }

    public void addElement(String element) {
        elements.put(element, new DefaultMutableTreeNode(element));
    }

    public DefaultMutableTreeNode getElement(String element) {
        return elements.getOrDefault(element, null);
    }

}
