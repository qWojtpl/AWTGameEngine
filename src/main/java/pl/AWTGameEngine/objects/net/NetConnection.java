package pl.AWTGameEngine.objects.net;

import java.io.IOException;

public abstract class NetConnection {

    protected long id;

    public NetConnection(long id) {
        this.id = id;
    }

    public abstract void sendMessage(String message);

    public void sendBlock(NetBlock block) {
        sendMessage(block.formMessage());
    }

    public abstract void close() throws IOException;

    public long getId() {
        return this.id;
    }

}