package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.text.MessageFormat;

public class WebGraphicsManager {

    private WebView webView;

    public WebGraphicsManager(WebView webView) {
        this.webView = webView;
    }

    public void updatePosition(GameObject object) {
        Camera camera = object.getPanel().getCamera();
        execute(MessageFormat.format("setPosition(\"{0}\", \"{1}\", \"{2}\");",
                object.getIdentifier(),
                camera.parseX(object, object.getX()),
                camera.parseY(object, object.getY())));
    }

    public void updateSize(GameObject object) {
        Camera camera = object.getPanel().getCamera();
        execute(MessageFormat.format("setSize(\"{0}\", \"{1}\", \"{2}\");",
                object.getIdentifier(),
                camera.parseScale(object.getSizeX()),
                camera.parseScale(object.getSizeY())));
    }

    public void updateRotation(GameObject object) {
        execute(MessageFormat.format("setRotation(\"{0}\", \"{1}\");",
                object.getIdentifier(), object.getRotation()));
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
