package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.ObjectComponent;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameObject {

    private final String identifier;
    private int x = 0;
    private int y = 0;
    private int scaleX = 100;
    private int scaleY = 100;
    private int priority = 0;
    private final List<ObjectComponent> components = new ArrayList<>();

    public GameObject(String identifier) {
        this.identifier = identifier;
    }

    public void addComponent(ObjectComponent component) {
        this.components.add(component);
        component.onAddComponent();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public List<ObjectComponent> getComponents() {
        return this.components;
    }

    public List<ObjectComponent> getComponentsByClass(Class<?> clazz) {
        List<ObjectComponent> componentList = new ArrayList<>();
        for(ObjectComponent component : components) {
            if(component.getClass().equals(clazz)) {
                componentList.add(component);
            }
        }
        return componentList;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScaleX() {
        return this.scaleX;
    }

    public int getScaleY() {
        return this.scaleY;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean setX(int x) {
        if(!updatePosition(x, getY())) {
            return false;
        }
        this.x = x;
        return true;
    }

    public boolean setY(int y) {
        if(!updatePosition(getX(), y)) {
            return false;
        }
        this.y = y;
        return true;
    }

    public void setScaleX(int x) {
        this.scaleX = x;
    }

    public void setScaleY(int y) {
        this.scaleY = y;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void render(Graphics g) {
        for(ObjectComponent component : getComponents()) {
            component.onRender(g);
        }
    }

    public boolean updatePosition(int newX, int newY) {
        for(ObjectComponent component : getComponents()) {
            boolean good = component.onUpdatePosition(newX, newY);
            if(!good) {
                return false;
            }
        }
        return true;
    }

    public void deserialize(String data) {
        HashMap<String, String> properties = new HashMap<>();
        String key = "";
        String value = "";
        boolean valuesOpened = false;
        for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == '{') {
                valuesOpened = true;
                continue;
            } else if(data.charAt(i) == '}') {
                valuesOpened = false;
                properties.put(key, value);
                key = "";
                value = "";
                continue;
            }
            if(!valuesOpened) {
                key += data.charAt(i);
            } else {
                value += data.charAt(i);
            }
        }
        for(String propertyName : properties.keySet()) {
            if(propertyName.equalsIgnoreCase("pos")) {
                String[] split = properties.get(propertyName).split(";");
                if(split.length < 2) {
                    continue;
                }
                try {
                    this.setX(Integer.parseInt(split[0]));
                    this.setY(Integer.parseInt(split[1]));
                } catch(NumberFormatException ignored) {
                }
            } else if(propertyName.equalsIgnoreCase("priority")) {
                try {
                    this.setPriority(Integer.parseInt(properties.get(propertyName)));
                } catch(NumberFormatException ignored) {
                }
            } else if(propertyName.equalsIgnoreCase("scale")) {
                String[] split = properties.get(propertyName).split(";");
                if(split.length < 2) {
                    continue;
                }
                try {
                    this.setScaleX(Integer.parseInt(split[0]));
                    this.setScaleY(Integer.parseInt(split[1]));
                } catch(NumberFormatException ignored) {
                }
            } else if(propertyName.contains(":ObjectComponent")) {
                String className;
                if(propertyName.contains(":ObjectComponent-C")) {
                    className = "custom." + propertyName.replace(":ObjectComponent-C", "");
                } else {
                    className = "components." + propertyName.replace(":ObjectComponent", "");
                }
                LinkedHashMap<String, String> fields = new LinkedHashMap<>();
                String property = properties.get(propertyName);
                boolean fieldValueOpened = false;
                for(int i = 0; i < property.length(); i++) {
                    if(property.charAt(i) == '^') {
                        fieldValueOpened = !fieldValueOpened;
                        if(!fieldValueOpened) {
                            while(fields.containsKey(key)) {
                                key += "~";
                            }
                            fields.put(key, value);
                            key = "";
                            value = "";
                        }
                        continue;
                    }
                    if(!fieldValueOpened) {
                        key += property.charAt(i);
                    } else {
                        value += property.charAt(i);
                    }
                }
                try {
                    Class<? extends ObjectComponent> clazz = Class.forName("pl.AWTGameEngine." + className)
                            .asSubclass(ObjectComponent.class);
                    ObjectComponent o = clazz.getConstructor(GameObject.class).newInstance(this);
                    for(String fieldName : fields.keySet()) {
                        String v = fields.get(fieldName);
                        fieldName = fieldName.replace("~", "");
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        clazz.getDeclaredMethod(methodName, String.class).invoke(o, v);
                    }
                    this.addComponent(o);
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
