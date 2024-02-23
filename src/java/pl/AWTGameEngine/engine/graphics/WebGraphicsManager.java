package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import org.w3c.dom.Element;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

public class WebGraphicsManager extends GraphicsManager {

    private WebView webView;

    public WebGraphicsManager(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void drawImage(Sprite image, int x, int y, int width, int height, RenderOptions renderOptions) {
        readOptions(renderOptions);
        Platform.runLater(() -> {
            GameObject go = renderOptions.getContext();
            if(webView.getEngine().executeScript("typeof setPosition").equals("undefined")) {
                return;
            }
            webView.getEngine().executeScript(
                    "setPosition('" + go.getIdentifier() + "', " + x + ", " + y + ");");
            webView.getEngine().executeScript(
                    "setSize('" + go.getIdentifier() + "', " + width + ", " + height + ");");
            webView.getEngine().executeScript(
                    "drawImage('" + go.getIdentifier() + "', '" + image.getImagePath() + "');");

        });
        rollBackOptions();
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

}
