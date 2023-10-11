package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
        DefaultMutableTreeNode root = addElement("Objects");
        elements.put("root", root);
        tree = new JTree(root);
        tree.setBorder(BorderFactory.createEmptyBorder(8, 16, 1, 1));
        tree.setFocusable(false);
        tree.setFont(getWindow().getFont().deriveFont(18f));
        tree.setRootVisible(true);
        container = new NestedPanel(getObject());
        container.setBackground(Color.WHITE);
        updatePosition();
        container.setLayout(new BorderLayout());
        container.add(tree);
        tree.addMouseListener(getWindow().getMouseListener());
        getObject().getPanel().add(container);
    }

    @Override
    public void onRender(Graphics g) {
        if(container == null) {
            return;
        }
        updatePosition();
    }

    private void updatePosition() {
        container.setLocation(
                (int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        container.setSize(
                (int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
    }

    @Override
    public void onParentChange(GameObject oldParent) {
        oldParent.getPanel().remove(container);
        getObject().getPanel().add(container);
    }

    public DefaultMutableTreeNode addElement(String element) {
        DefaultMutableTreeNode e = new DefaultMutableTreeNode(element);
        elements.put(element, e);
        return e;
    }

    public void addElementTo(DefaultMutableTreeNode element, DefaultMutableTreeNode newParent) {
        newParent.add(element);
    }

    public void reload() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.reload(root);
    }

    public DefaultMutableTreeNode getElement(String element) {
        return elements.getOrDefault(element, null);
    }

}
