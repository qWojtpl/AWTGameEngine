package pl.AWTGameEngine.engine.loops;

import pl.AWTGameEngine.engine.steam.SteamManager;

public class SteamLoop extends BaseLoop {

    public SteamLoop() {
        super(null, "SteamLoop");
    }

    @Override
    protected void iteration() {
        SteamManager.getInstance().updateCallbacks();
    }

    @Override
    protected void everySecondIteration() {

    }

}
