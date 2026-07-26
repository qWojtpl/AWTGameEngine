package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.annotations.methods.EventMethod;
import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.objects.net.NetConnection;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.NetBlock;

import java.net.Socket;

/**
 * NetComponent is a class which inherits from ObjectComponent
 * and contains events which are common for components which
 * works in the network (e.g. onClientConnect, onSynchronize, onNetUpdate).
 * <br><br>
 * Available events:
 * <ul>
 * <li>{@link #onClientConnect(Server, NetConnection)}</li>
 * <li>{@link #onClientDisconnect(Server, NetConnection)}</li>
 * <li>{@link #onClientTryToConnect(Socket)}</li>
 * <li>{@link #onSynchronize()}</li>
 * <li>{@link #onSynchronizeReceived(String)}</li>
 * <li>{@link #onNetUpdate()}</li>
 * <li>{@link #canSynchronize()}</li>
 * <li>{@link #clearNetCache()}</li>
 * </ul>
 */
public abstract class NetComponent extends ObjectComponent {

    public NetComponent(GameObject object) {
        super(object);
    }

    @EventMethod
    public void onClientConnect(Server server, NetConnection client) {

    }

    @EventMethod
    public void onClientDisconnect(Server server, NetConnection client) {

    }

    /**
     * Event is fired when client tries to connect and after max client check is fired.
     * You can check authentication, blacklist or whitelist here.
     * Return null to let the client in, or return a string, to send a message to the client.
     * @param socket Client socket
     * @return null or join-disconnect message
     */
    @EventMethod
    public String onClientTryToConnect(Socket socket) {
        return null;
    }

    /**
     * Object synchronization is handled over a TCP connection to ensure that component will be updated.
     * @return NetBlock
     */
    @EventMethod
    public NetBlock onSynchronize() {
        return new NetBlock();
    }

    /**
     * Event is fired when synchronization is received in the component.
     * @param data Data from the NetBlock
     */
    @EventMethod
    public void onSynchronizeReceived(String data) {

    }

    @EventMethod
    public void onNetUpdate() {

    }

    /**
     * Before component synchronization, canSynchronize() will be fired.
     * Here you can check cache, if the component was updated.
     * @return State if component can be synchronized
     */
    public boolean canSynchronize() {
        return true;
    }

    /**
     * Method is called when net cache should be cleared (e.g. new client connected).
     * You can change here, for example, a boolean which points if the component was updated.
     */
    public void clearNetCache() {

    }

}
