package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.engine.BindingsManager;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.PanelRegistry;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.ObjectCache;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;
import pl.AWTGameEngine.windows.Window;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Every component field should have dedicated method to set the value of this field.
 * This is used for deserialization.
 * Name schema: setFieldname(String)
 */
public abstract class ObjectComponent {

    private final GameObject object;
    private final ObjectCache cache;

    public ObjectComponent(GameObject object) {
        this.object = object;
        this.cache = new ObjectCache(object);
    }

    public final GameObject getObject() {
        return this.object;
    }

    public final ObjectCache getCache() {
        return this.cache;
    }

    protected final KeyListener getKeyListener() {
        return getWindow().getKeyListener();
    }

    protected final MouseListener getMouseListener() {
        return getObject().getPanel().getMouseListener();
    }

    protected final ColliderRegistry getColliderRegistry() {
        return getScene().getColliderRegistry();
    }

    protected final PanelRegistry getPanelRegistry() {
        return getScene().getPanelRegistry();
    }

    public final BindingsManager getBindingsManager() {
        return getScene().getBindingsManager();
    }

    protected final Camera getCamera() {
        return getObject().getPanel().getCamera();
    }

    protected final Window getWindow() {
        return getScene().getWindow();
    }

    protected final Scene getScene() {
        return getObject().getScene();
    }

    protected final SceneLoader getSceneLoader() {
        return getWindow().getSceneLoader();
    }

    protected final GameObject getObjectByName(String name) {
        return getScene().getGameObjectByName(name);
    }

    /**
     * Method is called before frame draw.
     * PreUpdate is called first.
     */
    @EventMethod
    public void onPreUpdate() {

    }

    /**
     * Method is called before frame draw.
     * Update is called after PreUpdate.
     */
    @EventMethod
    public void onUpdate() {

    }

    /**
     * Method is called before frame draw.
     * AfterUpdate is called after Update.
     */
    @EventMethod
    public void onAfterUpdate() {

    }

    @EventMethod
    public void onStaticUpdate() {

    }

    @EventMethod
    public void onPreRender(GraphicsManager g) {

    }

    /**
     * Method is called while frame is drawing.
     */
    @EventMethod
    public void onRender(GraphicsManager g) {

    }

    @EventMethod
    public void onAfterRender(GraphicsManager g) {

    }

    /**
     * Method is called when X or Y of object is changed.
     * @return True when position can be updated. Return false to cancel position update.
     */
    @EventMethod
    public boolean onUpdatePosition(int newX, int newY) {
        return true;
    }

    @EventMethod
    public boolean onUpdateRotation(int newRotation) {
        return true;
    }

    /**
     * Method will be called when component will be added to the object.
     */
    @EventMethod
    public void onAddComponent() {

    }

    /**
     * Method will be called when component will be removed from the object.
     */
    @EventMethod
    public void onRemoveComponent() {

    }

    /**
     * Method will be called when collision will be registered.
     */
    @EventMethod
    public void onCollide(GameObject object) {

    }

    @EventMethod
    public void onButtonClick() {

    }

    @EventMethod
    public void onContextMenuOpen(int x, int y) {

    }

    @EventMethod
    public void onContextMenuClick(int option, int x, int y) {

    }

    @EventMethod
    public void onMenuBarClick(String path) {

    }

    @EventMethod
    public void onCreateGameObject(GameObject newObject) {

    }

    @EventMethod
    public void onRemoveGameObject(GameObject object) {

    }

    @EventMethod
    public void onUpdateGameObject(GameObject updatedObject) {

    }

    @EventMethod
    public void onAddChild(GameObject child) {

    }

    @EventMethod
    public void onRemoveChild(GameObject child) {

    }

    @EventMethod
    public void onParentChange(GameObject oldParent) {

    }

    @EventMethod
    public void onMouseClick() {

    }

    @EventMethod
    public void onMouseClick(GameObject object) {

    }

    @EventMethod
    public void onKeyType(int keyCode) {

    }

    @EventMethod
    public void onKeyType(char key) {

    }

    @EventMethod
    public void onTreeValueChange(GameObject object, String[] path) {

    }

    @EventMethod
    public void onWindowClosing() {

    }

    public final boolean isUnique() {
        return this.getClass().isAnnotationPresent(Unique.class);
    }

    public final boolean conflictsWith(Class<? extends ObjectComponent> component) {
        if(component == null) {
            return false;
        }
        if(this.getClass().isAnnotationPresent(ConflictsWith.class)) {
            if(this.getClass().getAnnotation(ConflictsWith.class).value().equals(component)) {
                return true;
            }
        }
        if(this.getClass().isAnnotationPresent(Conflicts.class)) {
            for(ConflictsWith conflictsWith : this.getClass().getAnnotation(Conflicts.class).value()) {
                if(conflictsWith.value().equals(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final HashMap<String, String> serialize() {
        HashMap<String, String> data = new HashMap<>();
        for(Method method : this.getClass().getMethods()) {
            if(!method.isAnnotationPresent(SerializationGetter.class)) {
                continue;
            }
            String methodName = method.getName();
            if(methodName.startsWith("is")) {
                methodName = methodName.replaceFirst("is", "");
            } else {
                methodName = methodName.replaceFirst("get", "");
            }
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            try {
                data.put(methodName, method.invoke(this).toString());
            } catch(Exception e) {
                System.out.println("Exception while serializing " + this.getClass().getName());
                e.printStackTrace();
            }
        }
        return data;
    }

}