package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.annotations.components.ComponentMeta;
import pl.AWTGameEngine.annotations.components.management.Conflicts;
import pl.AWTGameEngine.annotations.components.management.ConflictsWith;
import pl.AWTGameEngine.annotations.components.management.Requires;
import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.EventMethod;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.listeners.KeyListener;
import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;
import pl.AWTGameEngine.windows.Window;

import java.util.List;

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
        return getWindow().getMouseListener();
    }

    protected final ColliderRegistry getColliderRegistry() {
        return getScene().getColliderRegistry();
    }

    protected final Camera getCamera() {
        return getObject().getScene().getPanel().getCamera();
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

    protected final RenderEngine getRenderEngine() {
        return getObject().getScene().getRenderEngine();
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

    @EventMethod
    public void onEverySecond() {

    }

    @EventMethod
    public void onPhysicsPreUpdate() {

    }

    @EventMethod
    public void onPhysicsUpdate() {

    }

    @EventMethod
    public void onPhysicsAfterUpdate() {

    }

    /**
     * AfterUpdate is called after Update.
     */
    @EventMethod
    public void onAfterUpdate() {

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

    @EventMethod
    public void on3DRenderRequest(GraphicsManager3D g) {

    }

    @EventMethod
    public void onWebRenderRequest(WebGraphicsManager g) {

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

    //todo: fire this event for 2D
    @EventMethod
    public boolean onUpdateRotation(double newRotation) {
        return true;
    }

    @EventMethod
    public void onUpdateRotation() {

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
     * Method will be called when another component will be added to the object.
     */
    @EventMethod
    public void onAddComponent(ObjectComponent component) {

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
    public void onCollide(RigidBody collisionObject, List<ContactPoint> contactPoints) {

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

    @EventMethod
    public void onClientConnect(Server server, ConnectedClient client) {

    }

    @EventMethod
    public void onClientDisconnect(Server server, ConnectedClient client) {

    }

    @EventMethod
    public void onSerializationFinish() {

    }

    /**
     * Object synchronize is over a TCP connection to ensure that component will be updated.
     * onSynchronize event is executed only on server side.
     * @return NetBlock
     */
    @EventMethod
    public NetBlock onSynchronize() {
        return new NetBlock();
    }

    @EventMethod
    public void onSynchronizeReceived(String data) {

    }

    @EventMethod
    public void onNetUpdate() {

    }

    public boolean canSynchronize() {
        return true;
    }

    public void clearNetCache() {

    }

    public final boolean isUnique() {
        return this.getClass().isAnnotationPresent(Unique.class);
    }

    public final boolean hasRequiredClass() {
        return this.getClass().isAnnotationPresent(Requires.class);
    }

    public final Class<? extends ObjectComponent> getRequiredClass() {
        return this.getClass().getAnnotation(Requires.class).value();
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

    public final boolean isFXComponent() {
        return this.getClass().isAnnotationPresent(ComponentFX.class);
    }

    public final boolean isGLComponent() {
        return this.getClass().isAnnotationPresent(ComponentGL.class);
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