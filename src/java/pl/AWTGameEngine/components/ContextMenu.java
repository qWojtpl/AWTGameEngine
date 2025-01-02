package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Conflicts;
import pl.AWTGameEngine.annotations.ConflictsWith;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

@Unique
@Conflicts
({
        @ConflictsWith(Canvas.class),
        @ConflictsWith(BlankRenderer.class)
})
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
        if(menu == null) {
            return;
        }
        if(menu.isShowing()) {
            return;
        }
        if(!getMouseListener().isMouseReleased()) {
            return;
        }
        if(!getMouseListener().getReleaseEvent().isPopupTrigger()) {
            return;
        }
        MouseEvent mouseEvent = getObject().getPanel().getMouseListener().getReleaseEvent();
        menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        for(ObjectComponent component : getObject().getEventHandler().getComponents("onContextMenuOpen#int#int")) {
            component.onContextMenuOpen(getMouseListener().getMouseX(), getMouseListener().getMouseY());
        }
    }

    @Override
    public void onUpdate() {
        onStaticUpdate();
    }

    @SerializationSetter
    public void setNextItem(String content) {
        initMenu();
        int c = menu.getComponentCount();
        JMenuItem item = new JMenuItem(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for(ObjectComponent component : getObject().getEventHandler().getComponents("onContextMenuClick#int#int#int")) {
                    component.onContextMenuClick(c, getMouseListener().getMouseX(), getMouseListener().getMouseY());
                }
            }

        });
        item.setText(content);
        item.setFont(getWindow().getFont(18f));
        menu.add(item);
    }

    public void clearMenu() {
        menu = null;
        initMenu();
    }

    private void initMenu() {
        if(menu != null) {
            return;
        }
        menu = new JPopupMenu();
    }

}
