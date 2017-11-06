package com.andreas.rockpaperscissors.net;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptService extends Service {

    private final ServerSocket serverSocket;

    public AcceptService(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if (serverSocket == null) {
                    throw new NetException("ServerSocket was null");
                }
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        NetHandler.getInstance().addConnection(clientSocket);
                    } catch (IOException e) {
                        throw new NetException("Server failure");
                    }
                }
            }
        };
    }
}
