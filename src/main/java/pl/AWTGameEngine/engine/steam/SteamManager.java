package pl.AWTGameEngine.engine.steam;

import com.codedisaster.steamworks.*;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.WaitForSeconds;
import pl.AWTGameEngine.engine.helpers.ImageHelper;
import pl.AWTGameEngine.engine.loops.SteamLoop;
import pl.AWTGameEngine.objects.render.Sprite;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class SteamManager {

    private static SteamManager steamManager;
    private SteamLoop steamLoop;
    private boolean initialized = false;
    // Steam closeable objects
    private SteamUser user;
    private SteamUserStats stats;
    private SteamFriends friends;
    private SteamUtils utils;
    // Managers
    private SteamAchievementsManager achievementsManager;
    // Cache
    private final HashMap<SteamID, Sprite> playerAvatars = new HashMap<>();

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
            createLoop();
            createObjects();
            Logger.info("Connection initialized.");
            initialized = true;
        } catch(Exception e) {
            Logger.exception("Exception while initializing Steamworks API", e);
            if(Dependencies.getAppProperties().getPropertyAsBoolean("crashWithoutSteam")) {
                System.exit(100);
            }
        }
    }

    public void dispose() {
        Logger.info("Shutting down Steam API connection...");
        user.dispose();
        stats.dispose();
        friends.dispose();
        utils.dispose();
        SteamAPI.shutdown();
        steamLoop.kill();
        steamLoop = null;
        playerAvatars.clear();
    }

    public void updateCallbacks() {
        SteamAPI.runCallbacks();
    }

    private void createLoop() {
        steamLoop = new SteamLoop();
        steamLoop.setTargetFps(15);
        steamLoop.start();
    }

    private void createObjects() {
        SteamUserHandler userHandler = new SteamUserHandler();
        user = new SteamUser(userHandler);
        SteamStatsHandler statsHandler = new SteamStatsHandler();
        stats = new SteamUserStats(statsHandler);
        SteamFriendsHandler friendsHandler = new SteamFriendsHandler();
        friends = new SteamFriends(friendsHandler);
        SteamUtilsHandler utilsHandler = new SteamUtilsHandler();
        utils = new SteamUtils(utilsHandler);
    }

    public SteamLoop getSteamLoop() {
        return this.steamLoop;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public SteamUser getUser() {
        return user;
    }

    public SteamUserStats getUserStats() {
        return stats;
    }

    public SteamFriends getFriends() {
        return friends;
    }

    public SteamUtils getUtils() {
        return utils;
    }

    public Sprite getPlayerAvatarSprite(SteamID steamID) {
        if(playerAvatars.containsKey(steamID)) {
            return playerAvatars.get(steamID);
        }
        int width, height;
        ByteBuffer buffer;
        try {
            int avatarHandle;
            int attempts = 0;
            do {
                if(attempts >= 300) {
                    throw new RuntimeException("Avatar download timed out.");
                }
                avatarHandle = friends.getLargeFriendAvatar(steamID);
                new WaitForSeconds(0.01).here();
                attempts++;
            } while(avatarHandle == 0 || avatarHandle == -1);
            width = utils.getImageWidth(avatarHandle);
            height = utils.getImageHeight(avatarHandle);
            int bufferSize = width * height * 4;
            buffer = ByteBuffer.allocateDirect(bufferSize);
            utils.getImageRGBA(avatarHandle, buffer);
        } catch(SteamException e) {
            Logger.exception("Cannot get player avatar", e);
            return Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg");
        }
        Sprite sprite = new Sprite(ImageHelper.bytesToBufferedImage(buffer, width, height));
        playerAvatars.put(steamID, sprite);
        return sprite;
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

    public SteamAchievementsManager getAchievementsManager() {
        if(achievementsManager == null) {
            achievementsManager = new SteamAchievementsManager();
        }
        return achievementsManager;
    }

    static class SteamUserHandler implements SteamUserCallback { }
    static class SteamStatsHandler implements SteamUserStatsCallback { }
    static class SteamFriendsHandler implements SteamFriendsCallback { }
    static class SteamUtilsHandler implements SteamUtilsCallback { }

}
