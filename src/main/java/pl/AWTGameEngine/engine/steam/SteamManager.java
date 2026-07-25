package pl.AWTGameEngine.engine.steam;

import com.codedisaster.steamworks.*;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.loops.SteamLoop;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SteamManager {

    private static SteamManager steamManager;
    private SteamUserStats stats;
    private AchievementsManager achievementsManager;
    private SteamLoop steamLoop;

    SteamManager() {

    }

    public void init() {
        try {
            Logger.info("Initializing Steamworks API...");
            createSteamIdFile(Dependencies.getAppProperties().getPropertyAsInteger("steamAppId"));
            SteamLibraryLoader loader = new SteamLibraryLoaderLwjgl3();
            if (!SteamAPI.loadLibraries(loader)) {
                throw new RuntimeException("Cannot load native libraries!");
            }
            if(!SteamAPI.init()) {
                throw new RuntimeException("Steam not available.");
            }
            steamLoop = new SteamLoop();
            steamLoop.setTargetFps(15);
            steamLoop.start();
            createStats();
            Logger.info("Connection initialized.");
        } catch(Exception e) {
            Logger.exception("Exception while initializing Steamworks API", e);
            if(Dependencies.getAppProperties().getPropertyAsBoolean("crashWithoutSteam")) {
                System.exit(100);
            }
        }
    }

    public void dispose() {
        Logger.info("Shutting down Steam API connection...");
        stats.dispose();
        SteamAPI.shutdown();
        steamLoop.kill();
        steamLoop = null;
    }

    public void updateCallbacks() {
        SteamAPI.runCallbacks();
    }

    private void createStats() {
        SteamStatsHandler handler = new SteamStatsHandler();
        stats = new SteamUserStats(handler);
    }

    public SteamLoop getSteamLoop() {
        return this.steamLoop;
    }

    public SteamUserStats getUserStats() {
        return stats;
    }

    private void createSteamIdFile(int steamId) throws IOException {
        Path filePath = Paths.get("./steam_appid.txt");
        if(!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        try(FileWriter writer = new FileWriter(String.valueOf(filePath))) {
            writer.write(steamId + "");
        }
    }

    public static SteamManager getInstance() {
        if(steamManager == null) {
            steamManager = new SteamManager();
        }
        return steamManager;
    }

    public AchievementsManager getAchievementsManager() {
        if(achievementsManager == null) {
            achievementsManager = new AchievementsManager();
        }
        return achievementsManager;
    }

    public static class AchievementsManager {

        AchievementsManager() {

        }

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

    static class SteamStatsHandler implements SteamUserStatsCallback { }


}
