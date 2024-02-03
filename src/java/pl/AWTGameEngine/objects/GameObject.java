package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.PanelComponent;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.scenes.Scene;

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
    private NestedPanel panel;
    private EventHandler eventHandler = new EventHandler();
    private final List<GameObject> children = new ArrayList<>();
    private final List<ObjectComponent> components = new ArrayList<>();

    public GameObject(String identifier, Scene scene) {
        this.identifier = identifier;
        this.scene = scene;
    }

    public void addComponent(ObjectComponent component) {
        if(component.isUnique() && getComponentsByClass(component.getClass()).size() > 0) {
            System.out.println("Component " + component.getClass().getName() + " is unique, cannot add another...");
            return;
        }
        if(!component.getObject().equals(this)) {
            System.out.println("Component's object is wrong!");
            return;
        }
        for(ObjectComponent c : getComponents()) {
            if(c.conflictsWith(component.getClass()) || component.conflictsWith(c.getClass())) {
                DialogManager.createError(
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
        BindingsManager.removeBindingsByOwner(component);
        component.onRemoveComponent();
    }

    public boolean hasComponent(Class<? extends ObjectComponent> component) {
        return getComponentsByClass(component).size() > 0;
    }

    public HashMap<String, HashMap<String, String>> serializeComponents() {
        HashMap<String, HashMap<String, String>> data = new HashMap<>();
        for(ObjectComponent component : getComponents()) {
            String className = component.getClass().getName().replace("pl.AWTGameEngine.components.", "")
                    + ":ObjectComponent";
            if(!component.getClass().getName().startsWith("pl.AWTGameEngine.components")) {
                className = component.getClass().getName().replace("pl.AWTGameEngine.custom.", "") + ":ObjectComponent-C";
            }
            data.put(className, component.serialize());
        }
        return data;
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

    public String getSerializeString() {
        return getSerializeString(serializeComponents());
    }

    public String getSerializeString(HashMap<String, HashMap<String, String>> data) {
        StringBuilder serializeString = new StringBuilder(
                "pos{" + getX() + ";" + getY() + "}" +
                "size{" + getSizeX() + ";" + getSizeY() + "}" +
                "priority{" + getPriority() + "}" +
                (isActive() ? "" : "active{false}") +
                (getRotation() == 0 ? "" : "rotation{" + getRotation() + "}") +
                (getParent() != null ? "parent{" + getParent().getIdentifier() + "}" : ""));
        for(String componentName : data.keySet()) {
            serializeString.append(componentName);
            serializeString.append("{");
            for(String fieldName : data.get(componentName).keySet()) {
                serializeString.append(fieldName);
                serializeString.append("^");
                serializeString.append(data.get(componentName).get(fieldName));
                serializeString.append("^");
            }
            serializeString.append("}");
        }
        return serializeString.toString();
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
        if(componentList.size() == 0) {
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
        return getParent().getAbsoluteParent();
    }

    public NestedPanel getPanel() {
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
    }

    @BindingSetter
    public void setSizeX(String x) {
        setSizeX(Integer.parseInt(x));
    }

    public void setSizeY(int y) {
        this.sizeY = y;
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
        for(ObjectComponent component : getComponents()) {
            if(component.getClass().isAnnotationPresent(Parentless.class)) {
                System.out.println("Can't add parent to object " + identifier + ", object is parentless!");
                return;
            }
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

    public void setPanel(NestedPanel panel) {
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

    public boolean updatePosition(int newX, int newY) {
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#int#int")) {
            if(!component.onUpdatePosition(newX, newY)) {
                return false;
            }
        }
        return true;
    }

    public void deserialize(String data) {
        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean valuesOpened = false;
        for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == '{') {
                valuesOpened = true;
                continue;
            } else if(data.charAt(i) == '}') {
                valuesOpened = false;
                while(properties.containsKey(key.toString())) {
                    key.append("~");
                }
                properties.put(key.toString(), value.toString());
                key = new StringBuilder();
                value = new StringBuilder();
                continue;
            }
            if(!valuesOpened) {
                key.append(data.charAt(i));
            } else {
                value.append(data.charAt(i));
            }
        }
        for(String propertyName : properties.keySet()) {
            if(propertyName.equalsIgnoreCase("pos")) {
                String[] split = properties.get(propertyName).split(";");
                if(split.length < 2) {
                    continue;
                }
                this.setX(split[0]);
                this.setY(split[1]);
            } else if(propertyName.equalsIgnoreCase("active")) {
                if(!Boolean.parseBoolean(properties.get(propertyName))) {
                    this.setActive(false);
                }
            } else if(propertyName.equalsIgnoreCase("priority")) {
                this.setPriority(properties.get(propertyName));
            } else if(propertyName.equalsIgnoreCase("rotation")) {
                this.setRotation(properties.get(propertyName));
            } else if(propertyName.equalsIgnoreCase("parent")) {
                GameObject go = scene.getGameObjectByName(properties.get(propertyName));
                if(go != null) {
                    this.setParent(go);
                } else {
                    System.out.println("Can't find object: " + properties.get(propertyName));
                }
            } else if(propertyName.equalsIgnoreCase("size")) {
                String[] split = properties.get(propertyName).split(";");
                if(split.length < 2) {
                    continue;
                }
                this.setSizeX(split[0]);
                this.setSizeY(split[1]);
            } else if(propertyName.contains(":ObjectComponent")) {
                String className;
                if(propertyName.contains(":ObjectComponent-C")) {
                    className = propertyName.replace(":ObjectComponent-C", "");
                } else {
                    className = "pl.AWTGameEngine.components." + propertyName.replace(":ObjectComponent", "");
                }
                LinkedHashMap<String, String> fields = new LinkedHashMap<>();
                String property = properties.get(propertyName);
                boolean fieldValueOpened = false;
                for(int i = 0; i < property.length(); i++) {
                    if(property.charAt(i) == '^') {
                        fieldValueOpened = !fieldValueOpened;
                        if(!fieldValueOpened) {
                            while(fields.containsKey(key.toString())) {
                                key.append("~");
                            }
                            fields.put(key.toString(), value.toString());
                            key = new StringBuilder();
                            value = new StringBuilder();
                        }
                        continue;
                    }
                    if(!fieldValueOpened) {
                        key.append(property.charAt(i));
                    } else {
                        value.append(property.charAt(i));
                    }
                }
                try {
                    className = className.replace("~", "");
                    Class<? extends ObjectComponent> clazz = Class.forName(className)
                            .asSubclass(ObjectComponent.class);
                    ObjectComponent o = clazz.getConstructor(GameObject.class).newInstance(this);
                    for(String fieldName : fields.keySet()) {
                        String v = fields.get(fieldName);
                        fieldName = fieldName.replace("~", "");
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        if(clazz.getSuperclass().equals(ObjectComponent.class)) {
                            if(clazz.getDeclaredMethod(methodName, String.class).isAnnotationPresent(SerializationSetter.class)) {
                                clazz.getDeclaredMethod(methodName, String.class).invoke(o, v);
                            } else {
                                System.out.println("Tried to invoke " + methodName
                                        + " in serialization (" + className + "), but this method is not annotated as SerializationMethod");
                            }
                        } else {
                            if(clazz.getSuperclass().getDeclaredMethod(methodName, String.class).isAnnotationPresent(SerializationSetter.class)) {
                                clazz.getSuperclass().getDeclaredMethod(methodName, String.class).invoke(o, v);
                            } else {
                                System.out.println("Tried to invoke " + methodName
                                        + " in serialization (" + className + "), but this method is not annotated as SerializationMethod");
                            }
                        }
                    }
                    this.addComponent(o);
                } catch(Exception e) {
                    Logger.log("Exception while deserializing GameObject", e);
                }
            }
        }
    }

}