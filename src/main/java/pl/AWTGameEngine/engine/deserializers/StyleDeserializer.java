package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Node;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.scenes.Scene;

public class StyleDeserializer {

    public static void deserialize(Scene scene, String filePath) {
        scene.setCustomStyles(scene.getCustomStyles() +
                String.join("\n", Dependencies.getResourceManager().getResource(filePath)));
    }

    public static void deserialize(Scene scene, Node node) {
        scene.setCustomStyles(scene.getCustomStyles() + node.getTextContent());
    }

}
