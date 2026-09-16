package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Node;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.objects.render.Material;
import pl.AWTGameEngine.scenes.Scene;

public class MaterialDeserializer {

    public static void deserialize(String name, Scene scene, Node node) {
        scene.addMaterial(new Material(name)
                .setSprite(Dependencies.getResourceManager().getResourceAsSprite(node.getAttributes().getNamedItem("sprite").getNodeValue())));
    }

}
