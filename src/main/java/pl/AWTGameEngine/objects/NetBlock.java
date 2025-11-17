package pl.AWTGameEngine.objects;

public class NetBlock {

    private final String identifier;
    private final String component;
    private final String data;

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

    public NetBlock(String identifier, TransformSet position, TransformSet size, int netOwner) {
        this.identifier = identifier;
        this.component = null;
        this.data = position.toString() + size + "╚" + netOwner;
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

    public String formMessage() {
        return getIdentifier() + ";" + getComponent() + ";" + getData();
    }

}
