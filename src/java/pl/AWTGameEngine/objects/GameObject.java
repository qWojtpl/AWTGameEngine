package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.components.WebHandler;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.graphics.*;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.util.*;
import java.util.List;

public class GameObject {

    private final String identifier;
    private final Scene scene;
    private boolean active = true;
    private TransformSet position = new TransformSet(0, 0, 0);
    private TransformSet rotation = new TransformSet(0, 0, 0);
    private TransformSet size = new TransformSet(0, 0, 0);
    private int priority = 0;
    private PanelObject panel;
    private EventHandler eventHandler = new EventHandler();
    private final List<ObjectComponent> components = new ArrayList<>();

    public GameObject(String identifier, Scene scene) {
        this.identifier = identifier;
        this.scene = scene;
        if(Window.RenderEngine.WEB.equals(getScene().getWindow().getRenderEngine())) {
            this.addComponent(new WebHandler(this));
        }
    }

    public void addComponent(ObjectComponent component) {
        Window.RenderEngine renderEngine = scene.getWindow().getRenderEngine();
        if(Window.RenderEngine.WEB.equals(renderEngine)) {
            if(!component.isWebComponent()) {
                Logger.log(1, "Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as WebComponent");
                return;
            }
        } else if(Window.RenderEngine.DEFAULT.equals(renderEngine)) {
            if(!component.isDefaultComponent()) {
                Logger.log(1, "Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as DefaultComponent");
                return;
            }
        } else if(Window.RenderEngine.FX3D.equals(renderEngine)) {
            if(!component.is3DComponent()) {
                Logger.log(1, "Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as Component3D");
                return;
            }
        }
        if(component.isUnique() && !getComponentsByClass(component.getClass()).isEmpty()) {
            Logger.log(1, "Component " + component.getClass().getName() + " is unique, cannot add another!");
            return;
        }
        if(!component.getObject().equals(this)) {
            Logger.log(1, "Component " + component.getClass().getName() + " object is wrong!");
            return;
        }
        for(ObjectComponent c : getComponents()) {
            if(c.conflictsWith(component.getClass()) || component.conflictsWith(c.getClass())) {
                Logger.log(1,
                        "GameObject: " + getIdentifier() + "\nObject components: " + getComponents() + "\n" +
                                "Component " + c.getClass().getName() + " has conflict with "
                                + component.getClass().getName() + "; cannot add component " + component.getClass().getName());
                return;
            }
        }
        this.components.add(component);
        eventHandler.registerComponent(component);
        getScene().getSceneEventHandler().registerComponent(component);
        component.onAddComponent();
    }

    public void removeComponent(ObjectComponent component) {
        this.components.remove(component);
        eventHandler.removeComponent(component);
        getScene().getSceneEventHandler().removeComponent(component);
        component.onRemoveComponent();
    }

    public boolean hasComponent(Class<? extends ObjectComponent> component) {
        return getComponentsByClass(component).isEmpty();
    }

    public void moveX(double x) {
        double delta = x - this.position.getX();
        if(delta == 0) {
            return;
        }
        double direction = delta < 0 ? -1 : 1;
        for(double i = 0; i < Math.abs(delta); i++) {
            if(tryMoveX(direction)) {
                setX(this.position.getX() + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryMoveX(double direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdatePosition(this.position.getX() + direction, this.position.getY())) {
                return false;
            }
        }
        return true;
    }

    public void moveY(double y) {
        double delta = y - this.position.getY();
        if(delta == 0) {
            return;
        }
        double direction = delta < 0 ? -1 : 1;
        for(double i = 0; i < Math.abs(delta); i++) {
            if(tryMoveY(direction)) {
                setY(this.position.getY() + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryMoveY(double direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdatePosition(this.position.getX(), this.position.getY() + direction)) {
                return false;
            }
        }
        return true;
    }

    public void rotate(double angle) {
        double delta = angle - this.rotation.getX();
        if(delta == 0) {
            return;
        }
        double direction = delta < 0 ? -1 : 1;
        for(double i = 0; i < Math.abs(delta); i++) {
            if(tryRotate(direction)) {
                setRotationX(this.rotation.getX() + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryRotate(double direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdateRotation(this.rotation.getX() + direction)) {
                return false;
            }
        }
        return true;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Scene getScene() {
        return this.scene;
    }

    public List<ObjectComponent> getComponents() {
        return new ArrayList<>(this.components);
    }

    public List<ObjectComponent> getComponentsByClass(Class<? extends ObjectComponent> clazz) {
        List<ObjectComponent> componentList = new ArrayList<>();
        for(ObjectComponent component : components) {
            if(component.getClass().equals(clazz)) {
                componentList.add(component);
            }
        }
        return componentList;
    }

    public ObjectComponent getComponentByClass(Class<? extends ObjectComponent> clazz) {
        List<ObjectComponent> componentList = getComponentsByClass(clazz);
        if(componentList.isEmpty()) {
            return null;
        }
        return componentList.get(0);
    }

    public boolean isActive() {
        return this.active;
    }

    public double getX() {
        return this.position.getX();
    }

    public double getY() {
        return this.position.getY();
    }

    public double getZ() {
        return this.position.getZ();
    }

    public TransformSet getPosition() {
        return this.position;
    }

    public double getRotationX() {
        return this.rotation.getX();
    }

    public TransformSet getRotation() {
        return this.rotation;
    }

    public double getCenterX() {
        return getX() + getSizeX() / 2;
    }

    public double getCenterY() {
        return getY() + getSizeY() / 2;
    }

    public double getSizeX() {
        return this.size.getX();
    }

    public double getSizeY() {
        return this.size.getY();
    }

    public TransformSet getSize() {
        return this.size;
    }

    public int getPriority() {
        return this.priority;
    }

    public PanelObject getPanel() {
        return this.panel;
    }

    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setX(double x) {
        double delta = x - this.position.getX();
        if(delta == 0) {
            return;
        }
        this.position.setX(x);
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double")) {
            component.onUpdatePosition(x, this.position.getY());
        }
    }

    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(double y) {
        double delta = y - this.position.getY();
        if(delta == 0) {
            return;
        }
        this.position.setY(y);
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double")) {
            component.onUpdatePosition(this.position.getX(), y);
        }
    }

    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setZ(double z) {
        double delta = z - this.position.getZ();
        if(delta == 0) {
            return;
        }
        this.position.setZ(z);
        //todo
        /*for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double")) {
            component.onUpdatePosition(this.position.getX(), y);
        }*/
    }

    public void setZ(String z) {
        setZ(Integer.parseInt(z));
    }

    public void setPosition(TransformSet transform) {
        this.position = transform;
    }

    public void setRotationX(double angle) {
        this.rotation.setX(angle);
    }

    @Platform3D
    public void setRotationY(double angle) {
        this.rotation.setY(angle);
    }

    @Platform3D
    public void setRotationZ(double angle) {
        this.rotation.setZ(angle);
    }

    public void setRotationX(String angle) {
        setRotationX(Integer.parseInt(angle));
    }

    public void setRotation(TransformSet transform) {
        this.rotation = transform;
    }

    public void setSizeX(double x) {
        this.size.setX(x);
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double")) {
            component.onUpdateSize(x, this.size.getY());
        }
    }

    public void setSizeX(String x) {
        setSizeX(Integer.parseInt(x));
    }

    public void setSizeY(double y) {
        this.size.setY(y);
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double")) {
            component.onUpdateSize(this.size.getX(), y);
        }
    }

    public void setSizeY(String y) {
        setSizeY(Integer.parseInt(y));
    }

    public void setSizeZ(double z) {
        this.size.setZ(z);
        //todo
        /*for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double")) {
            component.onUpdateSize(this.size.getX(), y);
        }*/
    }

    public void setSizeZ(String z) {
        setSizeZ(Integer.parseInt(z));
    }

    public void setSize(TransformSet transform) {
        this.size = transform;
    }

    public void setPriority(int priority) {
        getScene().removeSortedObject(this.priority, this);
        this.priority = priority;
        getScene().addSortedObject(priority, this);
    }

    public void setPriority(String priority) {
        setPriority(Integer.parseInt(priority));
    }

    public void setPanel(PanelObject panel) {
        this.panel = panel;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void preRender(GraphicsManager g) {
        for(ObjectComponent component : eventHandler.getComponents("onPreRender#GraphicsManager")) {
            component.onPreRender(g);
        }
    }

    public void render(GraphicsManager g) {
        for(ObjectComponent component : eventHandler.getComponents("onRender#GraphicsManager")) {
            component.onRender(g);
        }
    }

    public void afterRender(GraphicsManager g) {
        for(ObjectComponent component : eventHandler.getComponents("onAfterRender#GraphicsManager")) {
            component.onAfterRender(g);
        }
    }

    public void webRender(WebGraphicsManager g) {
        for(ObjectComponent component : getComponents()) {
            if(component instanceof WebRenderable) {
                ((WebRenderable) component).onWebRenderRequest(g);
            }
        }
    }

    public void render3D(GraphicsManager3D g) {
        for(ObjectComponent component : getComponents()) {
            if(component instanceof Renderable3D) {
                ((Renderable3D) component).on3DRenderRequest(g);
            }
        }
    }

}