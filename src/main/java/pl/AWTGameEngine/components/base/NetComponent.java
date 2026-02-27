package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.annotations.methods.EventMethod;
import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.objects.ConnectedClient;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;

/**
 * NetComponent is a class which inherits from ObjectComponent
 * and contains events which are common for components which
 * works in the network (eg. onClientConnect, onSynchronize, onNetUpdate).
 */
public abstract class NetComponent extends ObjectComponent {

    public NetComponent(GameObject object) {
        super(object);
    }

    @EventMethod
    public void onClientConnect(Server server, ConnectedClient client) {

    }

    @EventMethod
    public void onClientDisconnect(Server server, ConnectedClient client) {

    }

    /**
     * Object synchronize is over a TCP connection to ensure that component will be updated.
     * onSynchronize event is executed only on server side.
     * @return NetBlock
     */
    @EventMethod
    public NetBlock onSynchronize() {
        return new NetBlock();
    }

    @EventMethod
    public void onSynchronizeReceived(String data) {

    }

    @EventMethod
    public void onNetUpdate() {

    }

    public boolean canSynchronize() {
        return true;
    }

    public void clearNetCache() {

    }

}
