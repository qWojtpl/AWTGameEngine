package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
public class MarqueeText extends HTMLComponent {

    public MarqueeText(GameObject object) {
        super(object);
    }

    @Override
    public String getRenderString() {
        return "<h1><marquee>Hello, World!</marquee></h1>";
    }

}
