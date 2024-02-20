package pl.AWTGameEngine.engine.panels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class WebPanel extends JFXPanel implements PanelObject {

    private final Window window;
    private WebView webView;
    private final Camera camera;
    private final WebGraphicsManager graphicsManager = new WebGraphicsManager();
    private MouseListener mouseListener;

    public WebPanel(Window window) {
        this.window = window;
        setLayout(null);
        setBackground(Color.WHITE);
        this.camera = new Camera(this);
        setMouseListener(new MouseListener(this));
        Platform.runLater(() -> {
            this.webView = new WebView();
            setScene(new Scene(webView));
            webView.getEngine().load("https://google.com");
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g == null || window.getCurrentScene() == null) {
            return;
        }
        graphicsManager.setGraphics(g);
        LinkedHashMap<Integer, java.util.List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        List<GameObject> renderList = new ArrayList<>();
        for (int i : sortedObjects.keySet()) {
            for (GameObject go : sortedObjects.get(i)) {
                if (!go.isActive()) {
                    continue;
                }
                if(this.equals(go.getPanel())) {
                    renderList.add(go);
                    go.preRender(graphicsManager);
                }
            }
        }
        for (GameObject go : renderList) {
            go.render(graphicsManager);
        }
        for (GameObject go : renderList) {
            go.afterRender(graphicsManager);
        }
    }

    public Window getWindow() {
        return this.window;
    }

    public WebView getWebView() {
        return this.webView;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public WebGraphicsManager getGraphicsManager() {
        return this.graphicsManager;
    }

    public MouseListener getMouseListener() {
        return this.mouseListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        if (this.mouseListener != null) {
            removeMouseListener(this.mouseListener);
            removeMouseMotionListener(this.mouseListener);
            removeMouseWheelListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

}
