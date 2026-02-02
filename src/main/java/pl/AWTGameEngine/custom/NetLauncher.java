package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.objects.GameObject;

@DefaultComponent
@WebComponent
@ComponentGL
@ComponentFX
public class NetLauncher extends ObjectComponent {

    public NetLauncher(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        Dependencies.getWindowsManager().createWindow("scenes/net/server.xml", RenderEngine.DEFAULT);
        getSceneLoader().loadSceneFile("scenes/net/client.xml");
    }

}
