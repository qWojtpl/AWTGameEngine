package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngine.windows.WindowsManager;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameLoop extends Thread implements WindowListener {

    private final Window window;
    private double FPS = 1;
    private boolean windowOpened = true;

    public GameLoop(Window window) {
        this.window = window;
    }

    @Override
    public void run() {
        while(windowOpened) {
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep((long) (1000 / getFPS()));
            } catch(InterruptedException ignored) {
                break;
            }
            window.getCurrentScene().update();
            for(NestedPanel panel : window.getCurrentScene().getPanelRegistry().getPanels()) {
                panel.repaint();
            }
        }
    }

    public double getFPS() {
        return FPS;
    }

    public void setFPS(double FPS) {
        if(FPS < 1) {
            FPS = 1;
        }
        this.FPS = FPS;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        windowOpened = true;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        windowOpened = false;
        WindowsManager.removeWindow(window);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

}