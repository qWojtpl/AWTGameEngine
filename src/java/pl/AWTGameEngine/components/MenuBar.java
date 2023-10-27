package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;
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

    public void setNextMenu(String menuName) {
        initBar();
        JMenu menu = new JMenu(menuName);
        menus.put(menuName, menu);
        menuBar.add(menu);
        activeMenu = menu;
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextSubMenu(String menuName) {
        initBar();
        JMenu menu = new JMenu(menuName);
        menus.put(getMenuNameByValue(activeMenu) + "-" + menuName, menu);
        activeMenu.add(menu);
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextItem(String itemName) {
        addItemToMenu(activeMenu, itemName);
    }

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
        menuBar.setFont(new Font("sans-serif", Font.PLAIN, 18));
        getWindow().setJMenuBar(menuBar);
    }

    public void initBar() {
        if(this.menuBar != null) {
            return;
        }
        this.menuBar = new JMenuBar();
        Font f = new Font("sans-serif", Font.PLAIN, 18);
        UIManager.put("Menu.font", f);
        UIManager.put("JMenuItem.font", f);
        getScene().getWindow().getPanel().setPreferredSize(
                new Dimension(
                        (int) (getScene().getWindow().getPanel().getPreferredSize().getWidth() + 18),
                        (int) getScene().getWindow().getPanel().getPreferredSize().getHeight())
                );
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
                for(ObjectComponent component : getObject().getComponents()) {
                    component.onMenuBarClick(getMenuNameByValue(menu) + "->" + itemName);
                }
            }

        });
        item.setFont(getWindow().getFont().deriveFont(18f));
        initBar();
        List<JMenuItem> i = getJMenuItems(getMenuNameByValue(menu));
        i.add(item);
        menuItems.put(menu, i);
        menu.add(item);
        updateWindow();
    }

}
