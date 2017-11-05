package com.andreas.rockpaperscissors.net;

import com.andreas.rockpaperscissors.util.Logger;
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
                    throw new Exception("AcceptService was started, but ServerSocket was null");
                }
                while (true) {
                    try {
                        Logger.log("Waiting for connection...");
                        Socket clientSocket = serverSocket.accept();
                        NetHandler.getInstance().addConnection(clientSocket);
                    } catch (IOException e) {
                        Logger.log("AcceptService failed");
                        System.err.println("Server failure");
                    }
                }
            }
        };
    }
}
