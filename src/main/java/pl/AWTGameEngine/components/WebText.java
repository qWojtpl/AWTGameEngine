package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.util.HashMap;

@WebComponent
public class WebText extends HTMLComponent {

    private String pattern;
    private int size;
    private ColorObject color;
    private final HashMap<String, String> values = new HashMap<>();

    public WebText(GameObject object) {
        super(object);
    }

    @SerializationSetter
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @SerializationSetter
    public void setValues(String values) {
        String[] split = values.split(",");
        for(String s : split) {
            String[] pair = s.split("=");
            this.values.put(pair[0], pair[1]);
        }
    }

    public void setValue(String key, String value) {
        values.put(key, value);
    }

    @SerializationSetter
    public void setColor(String color) {
        this.color = new ColorObject(color);
    }

    public void setColor(ColorObject color) {
        this.color = color;
    }

    @SerializationSetter
    public void setSize(String size) {
        setSize(Integer.parseInt(size));
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String getRenderString() {
        String text = pattern;
        for(String key : values.keySet()) {
            text = text.replace("{" + key + "}", values.get(key));
        }
        return "<h1 style=\"color:" + this.color.serialize() + ";font-size:" + size + "px\">" + text + "</h1>";
    }


}
