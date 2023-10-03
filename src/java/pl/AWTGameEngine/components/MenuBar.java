package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;

@Unique
public class MenuBar extends ObjectComponent {

    private java.awt.MenuBar menuBar;
    private Menu activeMenu;
    private Menu activeSubMenu;

    public MenuBar(GameObject object) {
        super(object);
    }

    public void setNextMenu(String menuName) {
        initBar();
        Menu menu = new Menu(menuName);
        menuBar.add(menu);
        activeMenu = menu;
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextSubMenu(String menuName) {
        initBar();
        Menu menu = new Menu(menuName);
        activeMenu.add(menu);
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextItem(String itemName) {
        initBar();
        MenuItem item = new MenuItem(itemName);
        activeMenu.add(item);
        updateWindow();
    }

    public void setNextSubItem(String itemName) {
        initBar();
        MenuItem item = new MenuItem(itemName);
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

}
