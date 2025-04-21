package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;
import pl.AWTGameEngine.windows.Window;

public abstract class ObjectComponent {

    private final GameObject object;

    public ObjectComponent(GameObject object) {
        this.object = object;
    }

    public final GameObject getObject() {
        return this.object;
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

    protected final Camera getCamera() {
        return getObject().getPanel().getCamera();
    }

    protected final Window getWindow() {
        return getScene().getWindow();
    }

    protected final Scene getScene() {
        return getObject().getScene();
    }

    protected final PanelObject getPanel() {
        return getObject().getPanel();
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
    public boolean onUpdatePosition(double newX, double newY) {
        return true;
    }

    @EventMethod
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        return true;
    }

    @EventMethod
    public boolean onUpdateSize(double newX, double newY) {
        return true;
    }

    @EventMethod
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        return true;
    }

    @EventMethod
    public boolean onUpdateRotation(double newRotation) {
        return true;
    }

    @EventMethod
    public boolean onUpdateRotation(double newX, double newY,  double newZ) {
        return true;
    }

    @EventMethod
    public void onUpdateCameraPosition(double newX, double newY) {

    }

    @EventMethod
    public void onUpdateCameraPosition(double newX, double newY, double newZ) {

    }

    @EventMethod
    public void onUpdateCameraRotation(double newX, double newY, double newZ) {

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
    public void onWindowResize(int newWidth, int newHeight) {

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

    public final boolean isDefaultComponent() {
        return this.getClass().isAnnotationPresent(DefaultComponent.class);
    }

    public final boolean isWebComponent() {
        return this.getClass().isAnnotationPresent(WebComponent.class);
    }

    public final boolean is3DComponent() {
        return this.getClass().isAnnotationPresent(Component3D.class);
    }

    public final String getComponentName() {
        String className = this.getClass().getSimpleName();
        if(this.getClass().isAnnotationPresent(ComponentMeta.class)) {
            String name = this.getClass().getAnnotation(ComponentMeta.class).name();
            if(name.isBlank()) {
                return className;
            }
            return name;
        }
        return className;
    }

    public final String getComponentDescription() {
        if(this.getClass().isAnnotationPresent(ComponentMeta.class)) {
            return this.getClass().getAnnotation(ComponentMeta.class).description();
        }
        return "No description provided.";
    }

    public final String getComponentAuthor() {
        if(this.getClass().isAnnotationPresent(ComponentMeta.class)) {
            return this.getClass().getAnnotation(ComponentMeta.class).author();
        }
        return "No author provided.";
    }

}