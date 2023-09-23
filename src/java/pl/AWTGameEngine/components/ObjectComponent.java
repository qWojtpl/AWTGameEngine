package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

/**
 * Every component field should have dedicated method to set the value of this field.
 * This is used for deserialization.
 * Name schema: setFieldname(String)
 */
public abstract class ObjectComponent {

    protected final GameObject object;
    protected boolean unique = false;

    public ObjectComponent(GameObject object) {
        this.object = object;
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

    public boolean isUnique() {
        return this.unique;
    }

    protected void setUnique(boolean unique) {
        this.unique = unique;
    }

}
