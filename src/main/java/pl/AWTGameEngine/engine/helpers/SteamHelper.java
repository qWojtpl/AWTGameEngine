package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.steam.SteamManager;

public class SteamHelper {

    public static boolean canInitSteam() {
        return Dependencies.getAppProperties().getPropertyAsBoolean("useSteamworks");
    }

    public static boolean isSteamAvailable() {
        return SteamManager.getInstance().isInitialized();
    }

}
