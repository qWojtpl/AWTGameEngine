package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;

@Unique
public class ContextMenu extends ObjectComponent {

    private JPopupMenu menu;

    public ContextMenu(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        initMenu();
    }

    @Override
    public void onStaticUpdate() {
        if(!menu.isShowing()) {
            if(getWindow().getMouseListener().isMouseReleased()) {
                if(getWindow().getMouseListener().getReleaseEvent().isPopupTrigger()) {
                    menu.show(getWindow().getMouseListener().getReleaseEvent().getComponent(),
                            getWindow().getMouseListener().getReleaseEvent().getX(),
                            getWindow().getMouseListener().getReleaseEvent().getY());
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        onStaticUpdate();
    }

    public void setNextItem(String content) {
        initMenu();
        JMenuItem item = new JMenuItem(content);
        item.setFont(getWindow().getFont().deriveFont(18f));
        menu.add(item);
    }

    private void initMenu() {
        if(menu == null) {
            menu = new JPopupMenu();
        }
    }

}
