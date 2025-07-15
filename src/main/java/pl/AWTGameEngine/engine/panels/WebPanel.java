package pl.AWTGameEngine.engine.panels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
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
            loadWebView();
        });
        setMouseListener(new MouseListener(this));
    }

    private void loadWebView() {
        Logger.log(0, "Loading WebView file...");
        StringBuilder htmlString = new StringBuilder();
        for(String line : Dependencies.getResourceManager().getResource(Dependencies.getAppProperties().getProperty("webViewPath") + "webview.html")) {
            if(line.contains("@{CUSTOM-USER-STYLES}")) {
                htmlString.append(window.getCurrentScene().getCustomStyles());
                continue;
            }
            htmlString.append(line);
        }
        Logger.log(0, "WebView file loaded.");
        webView.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            getWindow().getKeyListener().asKeyPress(event.getCode().getCode());
        });
        webView.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
            getWindow().getKeyListener().asKeyType(event.getCode().getCode());
        });
        webView.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
            getWindow().getKeyListener().asKeyRelease(event.getCode().getCode());
        });
        Logger.log(0, "Added listeners.");
        webView.getEngine().setJavaScriptEnabled(true);
        webView.contextMenuEnabledProperty().setValue(false);
        webView.getEngine().loadContent(htmlString.toString());
        graphicsManager = new WebGraphicsManager(webView);
        Logger.log(0, "WebView loaded.");
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager == null) {
            return;
        }
        for(GameObject go : getWindow().getCurrentScene().getGameObjects()) {
            go.webRender(graphicsManager);
        }
    }

    @Override
    public void updatePhysics() {

    }

    @Override
    public void unload() {
        setScene(null);
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
