package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.util.HashMap;

public class MenuBar extends ObjectComponent {

    private JMenuBar menuBar;
    private JMenu activeMenu;
    private JMenu activeSubMenu;
    private final HashMap<String, JMenu> menus = new HashMap<>();

    public MenuBar(GameObject object) {
        super(object);
    }

//    @Override
//    public void onAddComponent() {
//        this.menuBar = new JMenuBar();
//        setNextMenu("Menu");
//        setNextItem("Item 1");
//        JMenu menu = new JMenu("Menu");
//        JMenu submenu = new JMenu("Sub Menu");
//        JMenuItem i1 = new JMenuItem("Item 1");
//        JMenuItem i2 = new JMenuItem("Item 2");
//        JMenuItem i3 = new JMenuItem("Item 3");
//        JMenuItem i4 = new JMenuItem("Item 4");
//        JMenuItem i5 = new JMenuItem("Item 5");
//        menu.add(i1);
//        menu.add(i2);
//        menu.add(i3);
//        submenu.add(i4);
//        submenu.add(i5);
//        menu.add(submenu);
//        menuBar.add(menu);
//        menuBar.setFont(Main.getPanel().getFont().deriveFont(90f));
//        updateWindow();
//    }

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
        Main.getWindow().setJMenuBar(menuBar);
        Main.getWindow().setVisible(true);
    }

    public void initBar() {
        if(this.menuBar != null) {
            return;
        }
        this.menuBar = new JMenuBar();
    }

}
