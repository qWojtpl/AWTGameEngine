package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.windows.Window;

public class PhysicsLoop extends BaseLoop {

    public PhysicsLoop(Window window) {
        super(window);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while(window.getWindowListener().isOpened()) {
            try {
                Thread.sleep((long) (1000 / getFPS()));
            } catch(InterruptedException ignored) {
                break;
            }
            if(window.getCurrentScene() != null) {
                window.getPanel().updatePhysics();
            }
        }
    }

}
