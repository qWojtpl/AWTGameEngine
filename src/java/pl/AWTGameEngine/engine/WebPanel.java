package pl.AWTGameEngine.engine;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import pl.AWTGameEngine.windows.Window;

public class WebPanel extends JFXPanel {

    private final Window window;
    private WebView webView;

    public WebPanel(Window window) {
        this.window = window;
        Platform.runLater(() -> {
            this.webView = new WebView();
            setScene(new Scene(webView));
            webView.getEngine().load("https://google.com");
        });
    }

    public Window getWindow() {
        return this.window;
    }

    public WebView getWebView() {
        return this.webView;
    }

}
