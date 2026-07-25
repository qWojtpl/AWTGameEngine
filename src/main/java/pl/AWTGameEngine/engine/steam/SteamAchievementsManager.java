package pl.AWTGameEngine.engine.steam;

import com.codedisaster.steamworks.SteamUserStats;

public class SteamAchievementsManager {

    public void unlockAchievement(String name) {
        SteamUserStats stats = SteamManager.getInstance().getUserStats();
        stats.setAchievement(name);
        stats.storeStats();
    }

    public void takeAchievement(String name) {
        SteamUserStats stats = SteamManager.getInstance().getUserStats();
        stats.clearAchievement(name);
        stats.storeStats();
    }

}