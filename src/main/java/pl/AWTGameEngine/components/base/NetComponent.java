package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.annotations.methods.EventMethod;
import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.objects.net.ConnectedClient;
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
 * <li>{@link #onClientConnect(Server, ConnectedClient)}</li>
 * <li>{@link #onClientDisconnect(Server, ConnectedClient)}</li>
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
    public void onClientConnect(Server server, ConnectedClient client) {

    }

    @EventMethod
    public void onClientDisconnect(Server server, ConnectedClient client) {

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
