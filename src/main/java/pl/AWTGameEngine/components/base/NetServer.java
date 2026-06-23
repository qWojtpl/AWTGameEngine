package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.NetBlock;

import java.util.ArrayList;
import java.util.List;

public abstract class NetServer extends NetComponent {

    public NetServer(GameObject object) {
        super(object);
    }

    public void clearObjectCaches() {
        for(GameObject object : getScene().getGameObjects()) {
            object.getNet().clearCache();
        }

        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            ((NetComponent) component).clearNetCache();
        }

        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onClientDisconnect#Server#ConnectedClient")) {
//            ((NetComponent) component).onClientConnect(this, connectedClient); //todo
        }
    }

    public List<NetBlock> getObjectPositionBlocks() {
        List<NetBlock> blocks = new ArrayList<>();
        for(GameObject object : getScene().getGameObjects()) {
            NetBlock block = object.getNet().onPositionSynchronize();
            if(block.isEmpty()) {
                continue;
            }
            if(block.getIdentifier() != null) {
                blocks.add(block);
            } else {
                Logger.error("Incorrect NetBlock in " + object.getIdentifier());
            }
        }
        return blocks;
    }

    public List<NetBlock> getComponentBlocks() {
        List<NetBlock> blocks = new ArrayList<>();
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            NetComponent netComponent = (NetComponent) component;
            if(!netComponent.canSynchronize()) {
                continue;
            }
            NetBlock block = netComponent.onSynchronize();
            if(block.isEmpty()) {
                continue;
            }
            if(block.getIdentifier() != null && block.getComponent() != null) {
                blocks.add(block);
            } else {
                Logger.error("Incorrect NetBlock in " + component.getObject().getIdentifier() + " in component " + component.getClass().getName());
            }
        }
        return blocks;
    }

}
