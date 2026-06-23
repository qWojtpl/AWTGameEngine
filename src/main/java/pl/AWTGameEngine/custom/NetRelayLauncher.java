package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.objects.GameObject;

@DefaultComponent
@WebComponent
@ComponentGL
public class NetRelayLauncher extends ObjectComponent {

    public NetRelayLauncher(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        Dependencies.getWindowsManager().createWindow("scenes/net/relay/server.xml", RenderEngine.DEFAULT, true);
        getSceneLoader().loadSceneFile("scenes/net/relay/client.xml");
    }

}
