package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.steam.SteamManager;

public class SteamHelper {

    public static boolean isSteamAvailable() {
        return Dependencies.getAppProperties().getPropertyAsBoolean("useSteamworks") && SteamManager.getInstance().isInitialized();
    }

}
