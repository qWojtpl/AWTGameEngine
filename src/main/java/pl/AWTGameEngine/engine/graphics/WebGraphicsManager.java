package pl.AWTGameEngine.engine.graphics;

import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.FXHelper;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class WebGraphicsManager {

    private WebView webView;

    public WebGraphicsManager(WebView webView) {
        this.webView = webView;
    }

    public void updatePosition(GameObject object) {
        Camera camera = object.getScene().getPanel().getCamera();
        execute(String.format("setPosition(\"%s\", \"%s\", \"%s\");",
                object.getIdentifier(),
                camera.parseX(object, object.getX()),
                camera.parseY(object, object.getY())));
    }

    public void updateSize(GameObject object) {
        Camera camera = object.getScene().getPanel().getCamera();
        execute(String.format("setSize(\"%s\", \"%s\", \"%s\");",
                object.getIdentifier(),
                camera.parsePlainValue(object.getSizeX()),
                camera.parsePlainValue(object.getSizeY())));
    }

    public void updateRotation(GameObject object) {
        execute(String.format("setRotation(\"%s\", \"%s\");",
                object.getIdentifier(), object.getRotation().getX()));
    }

    public String execute(String script) {
        Object result = FXHelper.synchronizedCall(() -> {
            try {
                return webView.getEngine().executeScript(script);
            } catch(JSException e) {
                Logger.exception("Cannot execute script", e);
            }
            return null;
        });
        if(result == null) {
            return null;
        }
        return result.toString();
    }

    public String executeGetResult(String script) {
        return call(() -> webView.getEngine().executeScript(script)).toString();
    }

    private static <T> T call(Callable<T> task) {
        if (Platform.isFxApplicationThread()) {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        FutureTask<T> future = new FutureTask<>(task);
        Platform.runLater(future);

        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

}
