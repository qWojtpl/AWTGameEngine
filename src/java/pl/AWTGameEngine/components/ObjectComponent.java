package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.engine.PanelRegistry;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Every component field should have dedicated method to set the value of this field.
 * This is used for deserialization.
 * Name schema: setFieldname(String)
 */
public abstract class ObjectComponent {

    private final GameObject object;

    public ObjectComponent(GameObject object) {
        this.object = object;
    }

    protected GameObject getObject() {
        return this.object;
    }

    protected KeyListener getKeyListener() {
        return getWindow().getKeyListener();
    }

    protected ColliderRegistry getColliderRegistry() {
        return getScene().getColliderRegistry();
    }

    protected PanelRegistry getPanelRegistry() {
        return getScene().getPanelRegistry();
    }

    protected Camera getCamera() {
        return getScene().getCamera();
    }

    protected Window getWindow() {
        return getScene().getWindow();
    }

    protected Scene getScene() {
        return getObject().getScene();
    }

    protected SceneLoader getSceneLoader() {
        return getWindow().getSceneLoader();
    }

    protected GameObject getObjectByName(String name) {
        return getScene().getGameObjectByName(name);
    }

    /**
     * Method is called before frame draw.
     * PreUpdate is called first.
     */
    public void onPreUpdate() {

    }

    /**
     * Method is called before frame draw.
     * Update is called after PreUpdate.
     */
    public void onUpdate() {

    }

    /**
     * Method is called before frame draw.
     * AfterUpdate is called after Update.
     */
    public void onAfterUpdate() {

    }

    public void onStaticUpdate() {

    }

    /**
     * Method is called while frame is drawing.
     */
    public void onRender(Graphics g) {

    }

    /**
     * Method is called when X or Y of object is changed.
     * @return True when position can be updated. Return false to cancel position update.
     */
    public boolean onUpdatePosition(int newX, int newY) {
        return true;
    }

    /**
     * Method will be called when component will be added to the object.
     */
    public void onAddComponent() {

    }

    /**
     * Method will be called when component will be removed from the object.
     */
    public void onRemoveComponent() {

    }

    /**
     * Method will be called when collision will be registered.
     */
    public void onCollide() {

    }

    public void onButtonClick() {

    }

    public void onContextMenuClick(int option, int x, int y) {

    }

    public void onCreateGameObject(GameObject newObject) {

    }

    public void onUpdateGameObject(GameObject updatedObject) {

    }

    public void onAddChild(GameObject child) {

    }

    public void onRemoveChild(GameObject child) {

    }

    public void onParentChange(GameObject oldParent) {

    }

    public boolean isUnique() {
        return this.getClass().isAnnotationPresent(Unique.class);
    }

}
