package pl.AWTGameEngine.engine.graphics;

import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.FXHelper;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.GameObject;

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

    public void declareVariable(String variable, String value) {
        execute(String.format("var %s = %s", variable, value));
    }

    public String getFeedbacks(String identifier, Class<? extends ObjectComponent> clazz) {
        return execute(String.format("getFeedbacks(\"%s\", \"%s\");", identifier, clazz.getCanonicalName()));
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

    public WebView getWebView() {
        return this.webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

}
