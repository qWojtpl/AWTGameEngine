package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.TextArea;
import pl.AWTGameEngine.objects.BindableProperty;

import java.util.ArrayList;
import java.util.List;

public class BindingsManager {

    private static final List<BindableProperty> bindableProperties = new ArrayList<>();

    BindingsManager() {

    }

    public static void addBindableProperty(BindableProperty bindableProperty) {
        bindableProperties.add(bindableProperty);
    }

    public static void removeBindableProperty(BindableProperty bindableProperty) {
        bindableProperties.remove(bindableProperty);
    }

    public static void removeBindingsByOwner(Object owner) {
        for(BindableProperty property : getBindableProperties()) {
            if(property.getOwner().equals(owner)) {
                removeBindableProperty(property);
            }
        }
    }

    public static List<BindableProperty> getBindableProperties() {
        return new ArrayList<>(bindableProperties);
    }

    public static void updateBindings() {
        for(BindableProperty property : getBindableProperties()) {
            try {
                String result = String.valueOf(property.getMethods()[0].invoke(property.getObjects()[0]));
                if(property.getObjects()[1] instanceof TextArea) {
                    if(((TextArea) property.getObjects()[1]).isFocused()) {
                        continue;
                    }
                }
                property.getMethods()[1].invoke(property.getObjects()[1], result);
            } catch(Exception e) {
                Logger.log("Cannot update binding: " + property.getConnectionString(), e);
            }
        }
    }

}
