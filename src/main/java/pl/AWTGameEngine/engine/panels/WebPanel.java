package pl.AWTGameEngine.engine.panels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.event.KeyAdapter;

public class WebPanel extends JFXPanel implements PanelObject {

    private final Scene scene;
    private final Window window;
    private WebView webView;
    private final Camera camera;
    private WebGraphicsManager graphicsManager;
    private MouseListener mouseListener;

    public WebPanel(Scene scene) {
        this.scene = scene;
        this.window = scene.getWindow();
        setLayout(null);
        setBackground(Color.BLACK);

        this.camera = new Camera(this);

        if(window.isServerWindow()) {
            return;
        }

        Platform.runLater(() -> {
            this.webView = new WebView();

            // transparent
            javafx.scene.Scene fxScene = new javafx.scene.Scene(webView, new javafx.scene.paint.Color(0, 0, 0, 0));
            webView.setStyle("-fx-background-color: transparent;");
            webView.getEngine().setUserStyleSheetLocation("data:,body{background:transparent !important;}");
            com.sun.webkit.WebPage webPage = com.sun.javafx.webkit.Accessor.getPageFor(webView.getEngine());
            webPage.setBackgroundColor((new javafx.scene.paint.Color(0, 0, 0, 0)).hashCode());
            //

            webView.setFocusTraversable(false);

            webView.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, (event) -> {
                getWindow().getKeyListener().asKeyPress(event.getCode().getCode());
                getWindow().getKeyListener().asKeyType(event.getCode().getCode());
            });
            webView.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, (event) -> {
                getWindow().getKeyListener().asKeyType(event.getCharacter().charAt(0));
            });
            webView.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, (event) -> {
                getWindow().getKeyListener().asKeyRelease(event.getCode().getCode());
            });

            setScene(fxScene);
            loadWebView();
        });

        setMouseListener(new MouseListener(window));
    }

    private void loadWebView() {
        Logger.info("Loading WebView file...");
        StringBuilder htmlString = new StringBuilder();
        for(String line : Dependencies.getResourceManager().getResource(Dependencies.getAppProperties().getProperty("webViewPath") + "webview.html")) {
            if(line.contains("@{CUSTOM-USER-STYLES}")) {
                htmlString.append(scene.getCustomStyles());
                continue;
            }
            htmlString.append(line);
        }
        Logger.info("WebView file loaded.");
        webView.getEngine().setJavaScriptEnabled(true);
        webView.contextMenuEnabledProperty().setValue(false);
        webView.getEngine().loadContent(htmlString.toString());
        graphicsManager = new WebGraphicsManager(webView);
        Logger.info("WebView loaded.");
    }

    @Override
    public void updateRender() {
        if(window.getCurrentScene() == null) {
            return;
        }
        if(graphicsManager == null) {
            return;
        }
        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onWebRenderRequest#WebGraphicsManager")) {
            component.onWebRenderRequest(graphicsManager);
        }
    }

    @Override
    public void updatePhysics() {
        if(getWindow().getCurrentScene() == null) {
            return;
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsPreUpdate")) {
            component.onPhysicsPreUpdate();
        }

        PhysXManager.getInstance().simulateFrame(getWindow().getPhysicsLoop().getTargetFps());

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsUpdate")) {
            component.onPhysicsUpdate();
        }

        for(ObjectComponent component : scene.getSceneEventHandler().getComponents("onPhysicsAfterUpdate")) {
            component.onPhysicsAfterUpdate();
        }
    }

    @Override
    public void unload() {
        setScene(null);
        window.remove(this);
    }

    @Override
    public Scene getParentScene() {
        return this.scene;
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

    @Override
    public void printToGraphics(Graphics2D g) {
        super.print(g);
    }

}
