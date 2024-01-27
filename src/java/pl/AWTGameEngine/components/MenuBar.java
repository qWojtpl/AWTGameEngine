package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Unique
public class MenuBar extends ObjectComponent {

    private JMenuBar menuBar;
    private JMenu activeMenu;
    private JMenu activeSubMenu;
    private final HashMap<String, JMenu> menus = new HashMap<>();
    private final HashMap<JMenu, List<JMenuItem>> menuItems = new HashMap<>();

    public MenuBar(GameObject object) {
        super(object);
    }

    @Override
    public void onRemoveComponent() {
        getWindow().setMenuBar(null);
        getWindow().setJMenuBar(null);
    }

    @SerializationSetter
    public void setNextMenu(String menuName) {
        initBar();
        JMenu menu = new JMenu(menuName);
        menus.put(menuName, menu);
        menuBar.add(menu);
        activeMenu = menu;
        activeSubMenu = menu;
        updateWindow();
    }

    @SerializationSetter
    public void setNextSubMenu(String menuName) {
        initBar();
        JMenu menu = new JMenu(menuName);
        menus.put(getMenuNameByValue(activeMenu) + "-" + menuName, menu);
        activeMenu.add(menu);
        activeSubMenu = menu;
        updateWindow();
    }

    @SerializationSetter
    public void setNextItem(String itemName) {
        addItemToMenu(activeMenu, itemName);
    }

    @SerializationSetter
    public void setNextSubItem(String itemName) {
        initBar();
        JMenuItem item = new JMenuItem(itemName);
        List<JMenuItem> i = getJMenuItems(getMenuNameByValue(activeSubMenu));
        i.add(item);
        menuItems.put(activeSubMenu, i);
        activeSubMenu.add(item);
        updateWindow();
    }

    public void updateWindow() {
        menuBar.setFont(getWindow().getFont());
        getWindow().setJMenuBar(menuBar);
    }

    public void initBar() {
        if(this.menuBar != null) {
            return;
        }
        this.menuBar = new JMenuBar();
        UIManager.put("Menu.font", getWindow().getFont());
        UIManager.put("JMenuItem.font", getWindow().getFont());
    }

    public String getMenuNameByValue(JMenu menu) {
        for(String key : menus.keySet()) {
            if(menus.get(key).equals(menu)) {
                return key;
            }
        }
        return null;
    }

    public List<JMenuItem> getJMenuItems(String menuName) {
        return new ArrayList<>(menuItems.getOrDefault(menus.getOrDefault(menuName, null), new ArrayList<>()));
    }

    public JMenu getMenu(String menuName) {
        return menus.getOrDefault(menuName, null);
    }

    public void addItemToMenu(JMenu menu, String itemName) {
        JMenuItem item = new JMenuItem(new AbstractAction(itemName) {

            @Override
            public void actionPerformed(ActionEvent e) {
                for(ObjectComponent component : getObject().getEventHandler().getComponents("onMenuBarClick#String")) {
                    component.onMenuBarClick(getMenuNameByValue(menu) + "->" + itemName);
                }
            }

        });
        item.setFont(getWindow().getFont());
        initBar();
        List<JMenuItem> i = getJMenuItems(getMenuNameByValue(menu));
        i.add(item);
        menuItems.put(menu, i);
        menu.add(item);
        updateWindow();
    }

}
