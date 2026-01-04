package pl.AWTGameEngine.objects;

import java.util.ArrayList;
import java.util.List;

public class NetBlock {

    private final String identifier;
    private final String component;
    private String data;

    public NetBlock() {
        this.identifier = null;
        this.component = null;
        this.data = null;
    }

    public NetBlock(String identifier, String component, String data) {
        this.identifier = identifier;
        this.component = component;
        this.data = data;
    }

    public NetBlock(String identifier, String component, Object... data) {
        this.identifier = identifier;
        this.component = component;
        formData(data);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getComponent() {
        return this.component;
    }

    public String getData() {
        return this.data;
    }

    private void formData(Object... objects) {
        final List<String> stringList = new ArrayList<>();
        for(Object o : objects) {
            stringList.add(o.toString());
        }
        this.data = String.join("╚", stringList);
    }

    public String formMessage() {
        return getIdentifier() + ";" + getComponent() + ";" + getData();
    }

}
