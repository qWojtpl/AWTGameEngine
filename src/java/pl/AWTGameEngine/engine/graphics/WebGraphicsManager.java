package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

public class WebGraphicsManager {

    private WebView webView;

    public WebGraphicsManager(WebView webView) {
        this.webView = webView;
    }

    public void updatePosition(GameObject object) {
        Camera camera = object.getPanel().getCamera();
        execute(String.format("setPosition(\"%s\", \"%s\", \"%s\");",
                object.getIdentifier(),
                camera.parseX(object, object.getX()),
                camera.parseY(object, object.getY())));
    }

    public void updateSize(GameObject object) {
        Camera camera = object.getPanel().getCamera();
        execute(String.format("setSize(\"%s\", \"%s\", \"%s\");",
                object.getIdentifier(),
                camera.parsePlainValue(object.getSizeX()),
                camera.parsePlainValue(object.getSizeY())));
    }

    public void updateRotation(GameObject object) {
        execute(String.format("setRotation(\"%s\", \"%s\");",
                object.getIdentifier(), object.getRotationX()));
    }

    public void execute(String script) {
        Platform.runLater(() -> {
            webView.getEngine().executeScript(script);
        });
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

}
