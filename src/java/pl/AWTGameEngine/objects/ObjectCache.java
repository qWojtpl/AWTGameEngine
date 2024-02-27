package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.windows.Window;

import java.util.Objects;

public class ObjectCache {

    public final GameObject gameObject;
    private int x;
    private int y;
    private int sizeX;
    private int sizeY;

    public ObjectCache(GameObject object) {
        this.gameObject = object;
    }

    public boolean isChanged() {
        if(gameObject.getScene().getWindow().getRenderEngine().equals(Window.RenderEngine.DEFAULT)) {
            return true;
        }
        if(gameObject.getX() != x) {
            return true;
        }
        if(gameObject.getY() != y) {
            return true;
        }
        if(gameObject.getSizeX() != sizeX) {
            return true;
        }
        if(gameObject.getSizeY() != sizeY) {
            return true;
        }
        return false;
    }

    public void save() {
        this.x = gameObject.getX();
        this.y = gameObject.getY();
        this.sizeX = gameObject.getSizeX();
        this.sizeY = gameObject.getSizeY();
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

}
