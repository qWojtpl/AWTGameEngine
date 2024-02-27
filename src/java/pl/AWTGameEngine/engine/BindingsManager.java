package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.TextArea;
import pl.AWTGameEngine.objects.BindableProperty;

import java.util.ArrayList;
import java.util.List;

public class BindingsManager {

    private final List<BindableProperty> bindableProperties = new ArrayList<>();
    private final List<BindingOperation> bindingsOperations = new ArrayList<>();

    public void addBindableProperty(BindableProperty bindableProperty) {
        bindableProperties.add(bindableProperty);
    }

    public void removeBindableProperty(BindableProperty bindableProperty) {
        bindableProperties.remove(bindableProperty);
    }

    public void removeBindingsByOwner(Object owner) {
        for(BindableProperty property : getBindableProperties()) {
            if(property.getOwner().equals(owner)) {
                removeBindableProperty(property);
            }
        }
    }

    public void addOperation(BindableProperty property, OperationType operation, int value) {
        bindingsOperations.add(new BindingOperation(property, operation, value));
    }

    public void addOperation(BindingOperation operation) {
        bindingsOperations.add(operation);
    }

    public List<BindableProperty> getBindableProperties() {
        return new ArrayList<>(bindableProperties);
    }

    public List<BindingOperation> getBindingsOperations() {
        return new ArrayList<>(bindingsOperations);
    }

    public List<BindingOperation> getBindingsOperations(BindableProperty property) {
        List<BindingOperation> operations = new ArrayList<>();
        for(BindingOperation operation : getBindingsOperations()) {
            if(operation.getProperty().equals(property)) {
                operations.add(operation);
            }
        }
        return operations;
    }

    public void updateBindings() {
        for(BindableProperty property : getBindableProperties()) {
            try {
                String result = String.valueOf(property.getMethods()[0].invoke(property.getObjects()[0]));
                if(property.getObjects()[1] instanceof TextArea) {
                    if(((TextArea) property.getObjects()[1]).isFocused()) {
                        continue;
                    }
                }
                try {
                    int intResult = Integer.parseInt(result);
                    for(BindingOperation operation : getBindingsOperations(property)) {
                        if(OperationType.ADD.equals(operation.getOperation())) {
                            intResult += operation.getValue();
                        }
                    }
                    result = String.valueOf(intResult);
                } catch(NumberFormatException ignored) {

                }
                property.getMethods()[1].invoke(property.getObjects()[1], result);
            } catch(Exception e) {
                Logger.log("Cannot update binding: " + property.getConnectionString(), e);
            }
        }
    }

    public enum OperationType {
        ADD
    }

    public static class BindingOperation {

        private final BindableProperty property;
        private final OperationType operation;
        private final int value;

        public BindingOperation(BindableProperty property, OperationType operation, int value) {
            this.property = property;
            this.operation = operation;
            this.value = value;
        }

        public BindableProperty getProperty() {
            return this.property;
        }

        public OperationType getOperation() {
            return this.operation;
        }

        public int getValue() {
            return this.value;
        }

    }

}
