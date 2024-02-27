package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import org.w3c.dom.Element;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.text.MessageFormat;

public class WebGraphicsManager extends GraphicsManager {

    private WebView webView;

    public WebGraphicsManager(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void drawImage(Sprite image, int x, int y, int width, int height, RenderOptions renderOptions) {
        Platform.runLater(() -> {
            GameObject object = renderOptions.getContext();
            webView.getEngine().executeScript(
                    MessageFormat.format("setPosition(\"{0}\", {1}, {2}); ", object.getIdentifier(), x, y) +
                            MessageFormat.format("setSize(\"{0}\", {1}, {2});", object.getIdentifier(), width, height));
        });

    }

    public WebView getWebView() {
        return this.webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

}
