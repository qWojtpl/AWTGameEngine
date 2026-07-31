package pl.AWTGameEngine.engine.panels;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.BaseWindow;
import pl.AWTGameEngine.windows.HeadlessWindow;
import pl.AWTGameEngine.windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class DefaultPanel extends JPanel implements PanelObject {

    private final BaseWindow window;
    private final Scene scene;
    private final Camera camera;
    private final GraphicsManager graphicsManager = new GraphicsManager();
    private final Canvas canvas;
    private BufferStrategy strategy;

    public DefaultPanel(Scene scene) {
        super(false);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        this.window = scene.getWindow();
        this.scene = scene;
        this.camera = new Camera(this);
        PhysXManager.getInstance().createScene(scene);
        this.canvas = new Canvas();
        canvas.setFocusable(false);
        add(this.canvas, BorderLayout.CENTER);
    }

    @Override
    public void updateRender() {

        if(strategy == null) {
            try {
                canvas.createBufferStrategy(2);
                strategy = canvas.getBufferStrategy();
            } catch(Exception e) {
                Logger.exception("Cannot create buffer strategy for default panel", e);
            }
        }

        if(window.getCurrentScene() == null || strategy == null) {
            return;
        }

        try {
            do {
                do {
                    Graphics2D g2d = (Graphics2D) strategy.getDrawGraphics();
                    if(g2d != null) {
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                        graphicsManager.setGraphics(g2d);

                        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPreRender#GraphicsManager")) {
                            component.onPreRender(graphicsManager);
                        }
                        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onRender#GraphicsManager")) {
                            component.onRender(graphicsManager);
                        }
                        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onAfterRender#GraphicsManager")) {
                            component.onAfterRender(graphicsManager);
                        }

                        g2d.dispose();
                    }
                } while(strategy.contentsRestored());

                strategy.show();

            } while(strategy.contentsLost());
        } catch(Exception e) {
            Logger.exception("Exception while drawing default rendering panel", e);
        }
    }

    @Override
    public void unload() {
        PhysXManager.getInstance().removeScene(scene);
        if(!(window instanceof HeadlessWindow)) {
            ((Window) window).remove(this);
        }
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
    }

    public BaseWindow getWindow() {
        return this.window;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public GraphicsManager getGraphicsManager() {
        return this.graphicsManager;
    }

    @Override
    public void printToGraphics(Graphics2D g) {
        if (canvas != null) {
            canvas.print(g);
        }
    }

    @Override
    public void onSceneLoad() {

    }

}
