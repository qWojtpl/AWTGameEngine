package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MenuBar extends ObjectComponent {

    private JMenuBar menuBar;
    private JMenu activeMenu;
    private JMenu activeSubMenu;

    public MenuBar(GameObject object) {
        super(object);
    }

    public void setNextMenu(String menuName) {
        initBar();
        JMenu menu = new JMenu(menuName);
        menuBar.add(menu);
        activeMenu = menu;
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextSubMenu(String menuName) {
        initBar();
        JMenu menu = new JMenu(menuName);
        activeMenu.add(menu);
        activeSubMenu = menu;
        updateWindow();
    }

    public void setNextItem(String itemName) {
        initBar();
        JMenuItem item = new JMenuItem(itemName);
        activeMenu.add(item);
        updateWindow();
    }

    public void setNextSubItem(String itemName) {
        initBar();
        JMenuItem item = new JMenuItem(itemName);
        activeSubMenu.add(item);
        updateWindow();
    }

    public void updateWindow() {
        menuBar.setVisible(true);
        getScene().getWindow().setJMenuBar(menuBar);
        getScene().getWindow().setVisible(true);
    }

    public void initBar() {
        if(this.menuBar != null) {
            return;
        }
        this.menuBar = new JMenuBar();
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
