package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.annotations.EventMethod;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventHandler {

    private final HashMap<String, List<ObjectComponent>> registeredEvents = new HashMap<>();

    public void registerComponent(ObjectComponent component) {
        for(Method method : component.getClass().getMethods()) {
            if(!isMethodOverridden(method)) {
                continue;
            }
            StringBuilder parameters = new StringBuilder();
            for(Class<?> parameter : method.getParameterTypes()) {
                parameters.append("#");
                parameters.append(parameter.getSimpleName());
            }
            String methodName = method.getName() + parameters;
            List<ObjectComponent> components = registeredEvents.getOrDefault(methodName, new ArrayList<>());
            components.add(component);
            registeredEvents.put(methodName, components);
        }
    }

    public void removeComponent(ObjectComponent component) {
        List<String> events = new ArrayList<>(registeredEvents.keySet());
        for(String event : events) {
            List<ObjectComponent> components = getComponents(event);
            components.remove(component);
            registeredEvents.put(event, components);
        }
    }

    public List<ObjectComponent> getComponents(String event) {
        return new ArrayList<>(registeredEvents.getOrDefault(event, new ArrayList<>()));
    }

    public List<ObjectComponent> getComponents(String event, GameObject gameObject) {
        List<ObjectComponent> components = new ArrayList<>();
        for(ObjectComponent component : getComponents(event)) {
            if(component.getObject().equals(gameObject)) {
                components.add(component);
            }
        }
        return components;
    }

    public HashMap<String, List<ObjectComponent>> getRegisteredEvents() {
        HashMap<String, List<ObjectComponent>> returnedEvents = new HashMap<>();
        for(String key : registeredEvents.keySet()) {
            List<ObjectComponent> list = new ArrayList<>(registeredEvents.get(key));
            returnedEvents.put(key, list);
        }
        return returnedEvents;
    }

    public static boolean isMethodOverridden(final Method myMethod) {
        Class<?> declaringClass = myMethod.getDeclaringClass();
        if(declaringClass.equals(Object.class)) {
            return false;
        }
        try {
            Method m = declaringClass.getSuperclass().getMethod(myMethod.getName(), myMethod.getParameterTypes());
            return m.isAnnotationPresent(EventMethod.class);
        } catch(NoSuchMethodException e) {
            return false;
        }
    }

}
