package pl.AWTGameEngine.objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.PanelComponent;
import pl.AWTGameEngine.components.WebHandler;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebRenderable;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.windows.Window;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class GameObject {

    private final String identifier;
    private final Scene scene;
    private boolean active = true;
    private int x = 0;
    private int y = 0;
    private int rotation = 0;
    private int sizeX = 100;
    private int sizeY = 100;
    private int priority = 0;
    private GameObject parent;
    private PanelObject panel;
    private EventHandler eventHandler = new EventHandler();
    private final List<GameObject> children = new ArrayList<>();
    private final List<ObjectComponent> components = new ArrayList<>();

    public GameObject(String identifier, Scene scene) {
        this.identifier = identifier;
        this.scene = scene;
        if(Window.RenderEngine.WEB.equals(getScene().getWindow().getRenderEngine())) {
            this.addComponent(new WebHandler(this));
        }
    }

    public void addComponent(ObjectComponent component) {
        if(Window.RenderEngine.WEB.equals(scene.getWindow().getRenderEngine())) {
            if(component.isNotOnWeb()) {
                Logger.log(1, "Component " + component.getClass().getName() + " is marked as NotOnWeb component, " +
                        "but render engine is set to Web. Find component dedicated for web rendering instead.");
                return;
            }
        } else {
            if(component.isOnlyOnWeb()) {
                Logger.log(1, "Component " + component.getClass().getName() + " is marked as OnlyOnWeb component, " +
                        "but render engine is set to Default. Find component dedicated for default rendering instead.");
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
        getScene().getBindingsManager().removeBindingsByOwner(component);
        component.onRemoveComponent();
    }

    public boolean hasComponent(Class<? extends ObjectComponent> component) {
        return getComponentsByClass(component).isEmpty();
    }

    public void addChild(GameObject object) {
        if(this.children.contains(object)) {
            return;
        }
        if(!this.equals(object.getParent())) {
            object.setParent(this);
            return;
        }
        this.children.add(object);
        for(ObjectComponent component : eventHandler.getComponents("onAddChild#GameObject")) {
            component.onAddChild(object);
        }
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onUpdateGameObject#boolean")) {
            component.onUpdateGameObject(this);
        }
    }

    public void removeChild(GameObject object) {
        if(!this.children.contains(object)) {
            return;
        }
        object.setPanel(getScene().getWindow().getPanel());
        this.children.remove(object);
        for(ObjectComponent component : eventHandler.getComponents("onRemoveChild#GameObject")) {
            component.onRemoveChild(object);
        }
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onUpdateGameObject#GameObject")) {
            component.onUpdateGameObject(this);
        }
    }

    public void moveX(int x) {
        int delta = x - this.x;
        if(delta == 0) {
            return;
        }
        int direction = delta < 0 ? -1 : 1;
        for(int i = 0; i < Math.abs(delta); i++) {
            if(tryMoveX(direction)) {
                setX(this.x + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryMoveX(int direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdatePosition(this.x + direction, this.y)) {
                return false;
            }
        }
        return true;
    }

    public void moveY(int y) {
        int delta = y - this.y;
        if(delta == 0) {
            return;
        }
        int direction = delta < 0 ? -1 : 1;
        for(int i = 0; i < Math.abs(delta); i++) {
            if(tryMoveY(direction)) {
                setY(this.y + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryMoveY(int direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdatePosition(this.x, this.y + direction)) {
                return false;
            }
        }
        return true;
    }

    public void rotate(int angle) {
        int delta = angle - this.rotation;
        if(delta == 0) {
            return;
        }
        int direction = delta < 0 ? -1 : 1;
        for(int i = 0; i < Math.abs(delta); i++) {
            if(tryRotate(direction)) {
                setRotation(this.rotation + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryRotate(int direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdateRotation(this.rotation + direction)) {
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

    @BindingGetter
    public int getX() {
        return this.x;
    }

    @BindingGetter
    public int getY() {
        return this.y;
    }

    @BindingGetter
    public int getRotation() {
        if(this.parent == null) {
            return this.rotation;
        }
        return this.rotation + this.parent.getRotation();
    }

    @BindingGetter
    public int getCenterX() {
        if(this.parent == null) {
            return getX() + getSizeX() / 2;
        }
        if(this.parent.hasComponent(PanelComponent.class)) {
            return getX() + getSizeX() / 2;
        }
        return this.parent.getCenterX();
    }

    @BindingGetter
    public int getCenterY() {
        if(this.parent == null) {
            return getY() + getSizeY() / 2;
        }
        if(this.parent.hasComponent(PanelComponent.class)) {
            return getY() + getSizeY() / 2;
        }
        return this.parent.getCenterY();
    }

    @BindingGetter
    public int getSizeX() {
        return this.sizeX;
    }

    @BindingGetter
    public int getSizeY() {
        return this.sizeY;
    }

    @BindingGetter
    public int getWidth() {
        int width = 0;
        for(GameObject object : getChildren()) {
            if(object.getX() + object.getSizeX() > width) {
                width = object.getX() + object.getSizeX();
            }
        }
        return width;
    }

    @BindingGetter
    public int getHeight() {
        int height = 0;
        for(GameObject object : getChildren()) {
            if(object.getChildren().size() > 0) {
                for(GameObject child : object.getChildren()) {
                    if(child.getY() + child.getSizeY() > height) {
                        height = child.getY() + child.getSizeY();
                    }
                }
            }
            if(object.getY() + object.getSizeY() > height) {
                height = object.getY() + object.getSizeY();
            }
        }
        return height;
    }

    @BindingGetter
    public int getChildrenHeight() {
        int height = 0;
        for(GameObject child : getChildren()) {
            height += child.getSizeY();
        }
        return height;
    }

    @BindingGetter
    public int getPriority() {
        return this.priority;
    }

    public GameObject getParent() {
        return this.parent;
    }

    public GameObject getAbsoluteParent() {
        if(getParent() == null) {
            return this;
        }
        if(getParent().hasComponent(PanelComponent.class)) {
            return this;
        }
        return getParent().getAbsoluteParent();
    }

    public PanelObject getPanel() {
        return this.panel;
    }

    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    public List<GameObject> getChildren() {
        return new ArrayList<>(children);
    }

    public List<GameObject> getAllChildren() {
        List<GameObject> r = new ArrayList<>();
        for(GameObject c : getChildren()) {
            r.add(c);
            r.addAll(c.getAllChildren());
        }
        return r;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setX(int x) {
        int delta = x - this.x;
        if(delta == 0) {
            return;
        }
        this.x = x;
        for(GameObject go : getChildren()) {
            go.setX(go.getX() + delta);
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#int#int")) {
            component.onUpdatePosition(x, this.y);
        }
    }

    @BindingSetter
    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(int y) {
        int delta = y - this.y;
        this.y = y;
        for(GameObject go : getChildren()) {
            go.setY(go.getY() + delta);
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#int#int")) {
            component.onUpdatePosition(this.x, y);
        }
    }

    @BindingSetter
    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setRotation(int angle) {
        this.rotation = angle;
    }

    @BindingSetter
    public void setRotation(String angle) {
        setRotation(Integer.parseInt(angle));
    }

    public void setSizeX(int x) {
        this.sizeX = x;
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#int#int")) {
            component.onUpdateSize(x, this.sizeY);
        }
    }

    @BindingSetter
    public void setSizeX(String x) {
        setSizeX(Integer.parseInt(x));
    }

    public void setSizeY(int y) {
        this.sizeY = y;
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#int#int")) {
            component.onUpdateSize(this.x, y);
        }
    }

    @BindingSetter
    public void setSizeY(String y) {
        setSizeY(Integer.parseInt(y));
    }

    public void setSize(int x, int y) {
        setSizeX(x);
        setSizeY(y);
    }

    public void setPriority(int priority) {
        getScene().removeSortedObject(this.priority, this);
        this.priority = priority;
        getScene().addSortedObject(priority, this);
    }

    @BindingSetter
    public void setPriority(String priority) {
        setPriority(Integer.parseInt(priority));
    }

    public void setParent(GameObject parent) {
        if(this.parent != null) {
            this.parent.removeChild(this);
        }
        GameObject oldParent = this.parent;
        this.parent = parent;
        for(ObjectComponent component : eventHandler.getComponents("onParentChange#GameObject")) {
            component.onParentChange(oldParent);
        }
        if(parent != null) {
            setPanel(parent.getPanel());
            parent.addChild(this);
        }
    }

    public void setPanel(PanelObject panel) {
        this.panel = panel;
        for(GameObject object : getChildren()) {
            object.setPanel(panel);
        }
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

    public void deserialize(Node data) {
        try {
            setX(Integer.parseInt(getValue(data, "x")));
            setY(Integer.parseInt(getValue(data, "y")));
            setSizeX(Integer.parseInt(getValue(data, "sizeX")));
            setSizeY(Integer.parseInt(getValue(data, "sizeY")));
            setRotation(Integer.parseInt(getValue(data, "rotation")));
            setPriority(Integer.parseInt(getValue(data, "priority")));
            if(getValue(data, "active").equals("0")) {
                setActive(true);
            } else {
                setActive(Boolean.parseBoolean(getValue(data, "active")));
            }
            for(int i = 0; i < data.getChildNodes().getLength(); i++) {
                Node childNode = data.getChildNodes().item(i);
                if(childNode.getNodeName().equals("object") || childNode.getNodeName().equals("#text")) {
                    continue;
                }
                if(childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String className = ((Element) childNode).getTagName();
                String pckg = getValue(childNode, "_package");
                if(!pckg.equals("0")) {
                    className = pckg + "." + className;
                } else {
                    className = "pl.AWTGameEngine.components." + className;
                }
                Class<? extends ObjectComponent> clazz = Class.forName(className)
                        .asSubclass(ObjectComponent.class);
                ObjectComponent o = clazz.getConstructor(GameObject.class).newInstance(this);
                if(childNode.getAttributes() == null) {
                    continue;
                }
                for(int j = 0; j < childNode.getAttributes().getLength(); j++) {
                    String fieldName = childNode.getAttributes().item(j).getNodeName();
                    if(fieldName.equals("_package")) {
                        continue;
                    }
                    String value = childNode.getAttributes().item(j).getNodeValue();
                    String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    if (clazz.getSuperclass().equals(ObjectComponent.class)) {
                        if (clazz.getDeclaredMethod(methodName, String.class).isAnnotationPresent(SerializationSetter.class)) {
                            clazz.getDeclaredMethod(methodName, String.class).invoke(o, value);
                        } else {
                            Logger.log(1, "Tried to invoke " + methodName
                                    + " in serialization (" + className + "), but this method is not annotated as SerializationMethod");
                        }
                    } else {
                        if (clazz.getSuperclass().getDeclaredMethod(methodName, String.class).isAnnotationPresent(SerializationSetter.class)) {
                            clazz.getSuperclass().getDeclaredMethod(methodName, String.class).invoke(o, value);
                        } else {
                            Logger.log(1, "Tried to invoke " + methodName
                                    + " in serialization (" + className + "), but this method is not annotated as SerializationMethod");
                        }
                    }
                }
                this.addComponent(o);
            }
        } catch(NumberFormatException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException | ClassNotFoundException | ClassCastException e) {
            Logger.log("Error while deserializing GameObject: " + getIdentifier(), e);
        }
    }

    private String getValue(Node node, String name) {
        if(node.getAttributes() == null) {
            return "0";
        }
        Node namedItem = node.getAttributes().getNamedItem(name);
        if(namedItem == null) {
            return "0";
        }
        return namedItem.getNodeValue();
    }

}