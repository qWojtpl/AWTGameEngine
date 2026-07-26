package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.*;
import pl.AWTGameEngine.components.base.NetServer;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.SteamHelper;
import pl.AWTGameEngine.engine.steam.SteamManager;
import pl.AWTGameEngine.objects.GameObject;

@ComponentGL
@WebComponent
@DefaultComponent
public class SteamRelayServer extends NetServer {

    public SteamRelayServer(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        if(!SteamHelper.isSteamAvailable()) {
            Logger.error("Cannot start SteamRelayServer, because Steam is not available.");
            return;
        }
        SteamManager.getInstance().initGameServer((short) 2701, (short) 2702, "1.0.0");
    }

    @Override
    public void onNetUpdate() {

    }

}
