package pl.AWTGameEngine.engine.panels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.util.*;
import java.util.List;

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
        this.camera = new Camera(this);
        Platform.runLater(() -> {
            this.webView = new WebView();
            setScene(new javafx.scene.Scene(webView));
        });
        setMouseListener(new MouseListener(this));
    }

    public void loadWebView() {
        Platform.runLater(() -> {
            StringBuilder htmlString = new StringBuilder();
            for(String line : Objects.requireNonNull(Dependencies.getResourceManager().getResource("webview/webview.html"))) {
                if(line.contains("@{CUSTOM-USER-STYLES}")) {
                    htmlString.append(window.getCurrentScene().getCustomStyles());
                    continue;
                }
                htmlString.append(line);
            }
            webView.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
                getWindow().getKeyListener().asKeyPress(event.getCode().getCode());
            });
            webView.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
                getWindow().getKeyListener().asKeyType(event.getCode().getCode());
            });
            webView.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
                getWindow().getKeyListener().asKeyRelease(event.getCode().getCode());
            });
            webView.getEngine().setJavaScriptEnabled(true);
            webView.contextMenuEnabledProperty().setValue(false);
            webView.getEngine().loadContent(htmlString.toString());
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
                if(!go.isActive()) {
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
