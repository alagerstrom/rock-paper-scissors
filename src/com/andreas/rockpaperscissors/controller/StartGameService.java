package com.andreas.rockpaperscissors.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class StartGameService extends Service {

    private final AppController appController;
    private final int port;
    private final String name;
    private final Consumer<Exception> onError;

    public StartGameService(AppController appController, int port, String name, Consumer<Exception> onError) {
        this.appController = appController;
        this.port = port;
        this.name = name;
        this.onError = onError;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    appController.setPortToUse(port);
                    appController.createNewGame(name);
                }catch (Exception e){
                    onError.accept(e);
                    throw new Exception("Failed to start game");
                }
                return null;
            }
        };
    }
}
