package pl.AWTGameEngine.components;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import pl.AWTGameEngine.annotations.components.types.*;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.NetServer;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.SteamHelper;
import pl.AWTGameEngine.engine.steam.SteamManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.NetBlock;
import pl.AWTGameEngine.objects.net.SteamNetConnection;

import java.util.HashMap;
import java.util.List;

@ComponentGL
@WebComponent
@DefaultComponent
public class SteamRelayServer extends NetServer {

    private int maxClients = 4;
    private final HashMap<SteamID, SteamNetConnection> connectedClients = new HashMap<>();

    public SteamRelayServer(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        if(!SteamHelper.isSteamAvailable()) {
            Logger.error("Cannot start SteamRelayServer, because Steam is not available.");
            return;
        }
        SteamManager.getInstance().createNetworkObjects();
        SteamManager.getInstance().setServerHandler(this);
        SteamManager.getInstance().getMatchmaking().createLobby(SteamMatchmaking.LobbyType.FriendsOnly, 4);
    }

    public void acceptClient(SteamID steamId) {
        Logger.info("Accepting client " + steamId + "...");
        SteamManager.getInstance().getNetwork().acceptP2PSessionWithUser(steamId);
        connectedClients.put(steamId, new SteamNetConnection(SteamID.getNativeHandle(steamId)));
    }

    public void rejectClient(SteamID steamId) {
        //todo
    }

    @Override
    public void onNetUpdate() {
        List<NetBlock> blocks = getObjectPositionBlocks();
        blocks.addAll(getComponentBlocks());
        for(SteamNetConnection connection : connectedClients.values()) {
            for(NetBlock block : blocks) {
                connection.sendBlock(block);
            }
        }
    }

    @SaveState(name = "maxClients")
    public int getMaxClients() {
        return this.maxClients;
    }

    @FromXML
    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

}
