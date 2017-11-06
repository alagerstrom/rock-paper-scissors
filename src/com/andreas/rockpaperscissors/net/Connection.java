package com.andreas.rockpaperscissors.net;

import com.andreas.rockpaperscissors.util.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends Service {

    private Socket socket;
    private NetHandler netHandler = NetHandler.getInstance();
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;


    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        start();
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Logger.log("Connection running");
                while (true){
                    try{
                        NetMessage netMessage = (NetMessage) inputStream.readObject();
                        netHandler.handleIncomingMessage(netMessage);
                    }catch (EOFException e){
                        Logger.log("Removing connection " + socket.getInetAddress() + ":" + socket.getPort());
                        netHandler.removeConnection(Connection.this);
                        break;
                    }catch (Exception e){
                        throw new NetException(e.getMessage());
                    }
                }
                return null;
            }
        };
    }

    public synchronized void send(NetMessage netMessage) throws IOException {
        outputStream.writeObject(netMessage);
    }
}
