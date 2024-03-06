package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.objects.GameObject;

import java.text.MessageFormat;

public class WebGraphicsManager {

    private WebView webView;

    public WebGraphicsManager(WebView webView) {
        this.webView = webView;
    }

    public void updatePosition(GameObject object) {
        execute(MessageFormat.format("setPosition(\"{0}\", \"{1}\", \"{2}\");",
                object.getIdentifier(), object.getX(), object.getY()));
    }

    public void updateSize(GameObject object) {
        execute(MessageFormat.format("setSize(\"{0}\", \"{1}\", \"{2}\");",
                object.getIdentifier(), object.getSizeX(), object.getSizeY()));
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
