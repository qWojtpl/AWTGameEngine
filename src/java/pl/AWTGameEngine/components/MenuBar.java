package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Unique
public class MenuBar extends ObjectComponent {

    private java.awt.MenuBar menuBar;
    private Menu activeMenu;
    private Menu activeSubMenu;
    private final HashMap<String, Menu> menus = new HashMap<>();
    private final HashMap<Menu, List<MenuItem>> menuItems = new HashMap<>();

    public MenuBar(GameObject object) {
        super(object);
    }

    public void setNextMenu(String menuName) {
        initBar();
        Menu menu = new Menu(menuName);
        menus.put(menuName, menu);
        menuBar.add(menu);
        activeMenu = menu;
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextSubMenu(String menuName) {
        initBar();
        Menu menu = new Menu(menuName);
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
        MenuItem item = new MenuItem(itemName);
        List<MenuItem> i = getMenuItems(getMenuNameByValue(activeSubMenu));
        i.add(item);
        menuItems.put(activeSubMenu, i);
        activeSubMenu.add(item);
        updateWindow();
    }

    public void updateWindow() {
        menuBar.setFont(new Font("sans-serif", Font.PLAIN, 18));
        getWindow().setMenuBar(menuBar);
    }

    public void initBar() {
        if(this.menuBar != null) {
            return;
        }
        this.menuBar = new java.awt.MenuBar();
        Font f = new Font("sans-serif", Font.PLAIN, 18);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        getScene().getWindow().getPanel().setPreferredSize(
                new Dimension(
                        (int) (getScene().getWindow().getPanel().getPreferredSize().getWidth() + 18),
                        (int) getScene().getWindow().getPanel().getPreferredSize().getHeight())
                );
    }

    public String getMenuNameByValue(Menu menu) {
        for(String key : menus.keySet()) {
            if(menus.get(key).equals(menu)) {
                return key;
            }
        }
        return null;
    }

    public List<MenuItem> getMenuItems(String menuName) {
        return new ArrayList<>(menuItems.getOrDefault(menus.getOrDefault(menuName, null), new ArrayList<>()));
    }

    public Menu getMenu(String menuName) {
        return menus.getOrDefault(menuName, null);
    }

    public void addItemToMenu(Menu menu, String itemName) {
        MenuItem item = new MenuItem(itemName);
        initBar();
        List<MenuItem> i = getMenuItems(getMenuNameByValue(menu));
        i.add(item);
        menuItems.put(menu, i);
        menu.add(item);
        updateWindow();
    }

}
