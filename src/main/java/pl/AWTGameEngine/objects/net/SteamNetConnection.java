package pl.AWTGameEngine.objects.net;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.steam.SteamManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SteamNetConnection extends NetConnection {

    private final SteamID steamId;

    public SteamNetConnection(long id) {
        super(id);
        this.steamId = SteamID.createFromNativeHandle(id);
    }

    public SteamID getSteamId() {
        return this.steamId;
    }

    @Override
    public void sendMessage(String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        try {
            SteamManager.getInstance().getNetwork().sendP2PPacket(steamId, buffer, SteamNetworking.P2PSend.Reliable, 0);
        } catch(SteamException e) {
            Logger.exception("Cannot send Steam P2P packet", e);
        }
    }

    @Override
    public void close() throws IOException {
        SteamManager.getInstance().getNetwork().closeP2PSessionWithUser(steamId);
    }

}
