package pl.AWTGameEngine.objects.net;

import java.util.ArrayList;
import java.util.List;

/** NetBlock is used as a frame to transport data through the network.<br>
 *  Structure is simple:
 *  <code>IDENTIFIER;COMPONENT;DATA</code><br>
 *  Where:<br>
 *  <code>IDENTIFIER</code> is GameObject's identifier,<br>
 *  <code>COMPONENT</code> is component's name (/w package),<br>
 *  <code>DATA is</code> data which is being sent<br>
 *  <code>╚</code> is a standard delimiter for data, which allows to send more than one
 *  information to component - use method <code>formData</code> to add delimiters
 */
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
        this.data = String.join(getDelimiter(), stringList);
    }

    public boolean isEmpty() {
        return this.identifier == null && this.component == null && this.data == null;
    }

    public String formMessage() {
        return getIdentifier() + getMessageStructureDelimiter() + getComponent() + getMessageStructureDelimiter() + getData();
    }

    public static String getMessageStructureDelimiter() {
        return ";";
    }

    public static String getDelimiter() {
        return "╚";
    }

}
