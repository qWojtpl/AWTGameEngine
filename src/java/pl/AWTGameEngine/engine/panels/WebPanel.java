package pl.AWTGameEngine.engine.panels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class WebPanel extends JFXPanel implements PanelObject {

    private final Window window;
    private WebView webView;
    private final Camera camera;
    private WebGraphicsManager graphicsManager;
    private MouseListener mouseListener;

    public WebPanel(Window window) {
        this.window = window;
        setLayout(null);
        setBackground(Color.WHITE);
        setFocusable(false);
        this.camera = new Camera(this);
        setMouseListener(new MouseListener(this));
        Platform.runLater(() -> {
            this.webView = new WebView();
            StringBuilder htmlString = new StringBuilder();
            for(String line : Objects.requireNonNull(ResourceManager.getResource("webview/webview.html"))) {
                htmlString.append(line);
            }
            webView.getEngine().loadContent(htmlString.toString());
            setScene(new javafx.scene.Scene(webView));
            webView.contextMenuEnabledProperty().setValue(false);
            graphicsManager = new WebGraphicsManager(webView);
        });
    }

    public void update() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager == null) {
            return;
        }
        LinkedHashMap<Integer, List<GameObject>> sortedObjects = window.getCurrentScene().getSortedObjects();
        for (int i : sortedObjects.keySet()) {
            for(GameObject go : sortedObjects.get(i)) {
                if (!go.isActive()) {
                    continue;
                }
                if(this.equals(go.getPanel())) {
                    go.webRender(graphicsManager);
                }
            }
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
