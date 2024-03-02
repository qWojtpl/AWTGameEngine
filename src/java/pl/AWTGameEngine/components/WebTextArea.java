package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.GameObject;

public class WebTextArea extends ObjectComponent {

    private String text = "Text";

    public WebTextArea(GameObject object) {
        super(object);
    }

    @SerializationGetter
    public String getText() {
        return this.text;
    }

    @SerializationSetter
    public void setText(String text) {
        this.text = text;
    }

}
