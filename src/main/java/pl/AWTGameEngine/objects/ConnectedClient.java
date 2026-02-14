package pl.AWTGameEngine.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedClient {

    private int id;
    private final Socket socket;
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;

    public ConnectedClient(int id, Socket socket) throws IOException {
        this.id = id;
        this.socket = socket;
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendInitMessage() {
        sendMessage(String.valueOf(id));
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public void sendBlock(NetBlock block) {
        sendMessage(block.formMessage());
    }

    public void close() throws IOException {
        printWriter.close();
        bufferedReader.close();
        socket.close();
    }

    public int getId() {
        return this.id;
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