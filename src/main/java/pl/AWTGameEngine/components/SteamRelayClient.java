package pl.AWTGameEngine.components;

import com.codedisaster.steamworks.SteamID;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.SteamHelper;
import pl.AWTGameEngine.engine.steam.SteamManager;
import pl.AWTGameEngine.objects.GameObject;

@ComponentGL
@WebComponent
@DefaultComponent
public class SteamRelayClient extends NetComponent {

    public SteamRelayClient(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        if(!SteamHelper.isSteamAvailable()) {
            Logger.error("Cannot start SteamRelayClient, because Steam is not available.");
            return;
        }
        SteamManager.getInstance().createNetworkObjects();
        SteamManager.getInstance().setClientHandler(this);
    }

    public void join(SteamID lobbyId, SteamID hostId) {
        SteamManager.getInstance().getMatchmaking().joinLobby(lobbyId);
    }

}
