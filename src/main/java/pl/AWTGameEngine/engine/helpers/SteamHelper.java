package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.Dependencies;

public class SteamHelper {

    public static boolean isSteamAvailable() {
        return Dependencies.getAppProperties().getPropertyAsBoolean("useSteamworks");
    }

}
