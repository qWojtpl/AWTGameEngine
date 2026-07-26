package pl.AWTGameEngine.objects.net;

import java.io.IOException;

public abstract class NetConnection {

    protected long id;

    public NetConnection(long id) {
        this.id = id;
    }

    public abstract void sendInitMessage();

    public abstract void sendMessage(String message);

    public abstract void sendBlock(NetBlock block);

    public abstract void close() throws IOException;

    public long getId() {
        return this.id;
    }

}