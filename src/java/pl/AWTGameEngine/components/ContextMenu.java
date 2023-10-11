package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

@Unique
public class ContextMenu extends ObjectComponent {

    private JPopupMenu menu;
    private MouseEvent mouseEvent;

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
                    mouseEvent = getWindow().getMouseListener().getReleaseEvent();
                    menu.show(mouseEvent.getComponent(),
                            mouseEvent.getX(),
                            mouseEvent.getY());
                    for(ObjectComponent component : getObject().getComponents()) {
                        component.onContextMenuOpen(
                                (int) (mouseEvent.getX() / getCamera().getZoom() + getCamera().getRelativeX(getObject())),
                                (int) (mouseEvent.getY() / getCamera().getZoom() + getCamera().getRelativeY(getObject())));
                    }
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
        int c = menu.getComponentCount();
        JMenuItem item = new JMenuItem(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for(ObjectComponent component : getObject().getComponents()) {
                    component.onContextMenuClick(c,
                            (int) (mouseEvent.getX() / getCamera().getZoom() + getCamera().getRelativeX(getObject())),
                            (int) (mouseEvent.getY() / getCamera().getZoom() + getCamera().getRelativeY(getObject())));
                }
            }

        });
        item.setText(content);
        item.setFont(getWindow().getFont().deriveFont(18f));
        menu.add(item);
    }

    private void initMenu() {
        if(menu == null) {
            menu = new JPopupMenu();
        }
    }

}
