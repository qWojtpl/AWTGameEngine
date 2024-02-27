package pl.AWTGameEngine.engine.panels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
        this.camera = new Camera(this);
        setMouseListener(new MouseListener(this));
        Platform.runLater(() -> {
            this.webView = new WebView();
            StringBuilder sceneString = new StringBuilder();
            for(String line : Objects.requireNonNull(ResourceManager.getResource(window.getCurrentScene().getName()))) {
                sceneString.append(line);
            }
            StringBuilder htmlString = new StringBuilder();
            for(String line : Objects.requireNonNull(ResourceManager.getResource("webview/webview.html"))) {
                if(line.contains("**SYS.OBJECTS**")) {
                    htmlString.append(sceneString);
                    continue;
                }
                htmlString.append(line);
            }
            webView.getEngine().loadContent(htmlString.toString());
            setScene(new Scene(webView));
            webView.contextMenuEnabledProperty().setValue(false);
            graphicsManager = new WebGraphicsManager(webView);
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(g == null || window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager == null) {
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
