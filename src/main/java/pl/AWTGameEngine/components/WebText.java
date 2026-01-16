package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.util.concurrent.ConcurrentHashMap;

@WebComponent
public class WebText extends HTMLComponent {

    private String pattern;
    private int size = 0;
    private int padding = 0;
    private ColorObject color;
    private final ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();

    public WebText(GameObject object) {
        super(object);
    }

    @FromXML
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @FromXML
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

    @FromXML
    public void setColor(String color) {
        this.color = new ColorObject(color);
    }

    public void setColor(ColorObject color) {
        this.color = color;
    }

    @FromXML
    public void setSize(String size) {
        setSize(Integer.parseInt(size));
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    @FromXML
    public void setPadding(String padding) {
        setPadding(Integer.parseInt(padding));
    }

    @Override
    public String getRenderString() {
        String text = pattern;
        for(String key : values.keySet()) {
            text = text.replace("{" + key + "}", values.get(key));
        }
        return "<h1 style=\"color:" + this.color.serialize() + ";font-size:" + size + "px;padding:" + padding + "px\">" + text + "</h1>";
    }


}
