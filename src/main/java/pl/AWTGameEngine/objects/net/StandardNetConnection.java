package pl.AWTGameEngine.objects.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class StandardNetConnection extends NetConnection {

    private final Socket socket;
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;

    public StandardNetConnection(long id, Socket socket) throws IOException {
        super(id);
        this.socket = socket;
        if(socket != null) {
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } else {
            this.printWriter = null;
            this.bufferedReader = null;
        }
    }

    @Override
    public void sendInitMessage() {
        sendMessage(String.valueOf(id));
    }

    @Override
    public void sendMessage(String message) {
        printWriter.println(message);
    }

    @Override
    public void sendBlock(NetBlock block) {
        sendMessage(block.formMessage());
    }

    @Override
    public void close() throws IOException {
        printWriter.close();
        bufferedReader.close();
        socket.close();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public PrintWriter getPrintWriter() {
        return this.printWriter;
    }

    public BufferedReader getBufferedReader() {
        return this.bufferedReader;
    }

    public void updateId(int id) {
        this.id = id;
    }

}
