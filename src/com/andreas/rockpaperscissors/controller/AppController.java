package com.andreas.rockpaperscissors.controller;

import com.andreas.rockpaperscissors.model.*;
import com.andreas.rockpaperscissors.util.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AppController {
    private final static AppController instance = new AppController();

    private Game game;
    private NetDelegate netDelegate;

    private AppController() {
        Logger.log("AppController created");
    }

    public static AppController getInstance() {
        return instance;
    }

    public void createNewGame(String playerName, int port, CompletionHandler completionHandler) {
        CompletableFuture.runAsync(()->{
            game = new Game(playerName);
            netDelegate = new NetDelegate(playerName);
            try {
                netDelegate.createServerSocket(port);
                netDelegate.addNetObserver(game);
                completionHandler.onSuccess();
            } catch (IOException e) {
                completionHandler.onFailure();
            }
        });
    }


    public void connectTo(String host, int port) throws IOException {
        netDelegate.connectTo(host, port);
        sendPlayerInfo();

    }

    public void getLocalHost(Consumer consumer) {
        consumer.accept("Getting IP...");
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String localHost = netDelegate.getLocalHost();
                        String tokens[] = localHost.split("/");
                        if (tokens.length < 1)
                            consumer.accept("");
                        consumer.accept(tokens[tokens.length - 1]);
                        return null;
                    }
                };
            }
        };
        service.start();
    }
    public int getLocalPort(){
        return netDelegate.getLocalPort();
    }

    public void sendPlayerInfo() {
        String playerName = game.getPlayerName();
        Message message = new Message(MessageType.PLAYER_INFO).setContent(playerName);
        sendMessageOnNewThread(message);
    }

    public void sendChatMessage(String messageContent) {
        String playerName = game.getPlayerName();
        Message message = new Message(MessageType.CHAT).setContent("[" + playerName + "] " + messageContent);
        sendMessageOnNewThread(message);
    }

    private void sendMessageOnNewThread(Message message) {
        netDelegate.sendMessageOnNewThread(message);
    }

    private void sendPlay(PlayCommand playCommand){
        Message message = new Message(MessageType.PLAY)
                .setSenderName(game.getPlayerName())
                .setPlayCommand(playCommand);
        sendMessageOnNewThread(message);
    }
    public void sendPlayRock() {
        sendPlay(PlayCommand.ROCK);
    }

    public void sendPlayPaper() {
        sendPlay(PlayCommand.PAPER);
    }

    public void sendPlayScissors() {
        sendPlay(PlayCommand.SCISSORS);
    }

    public void addGameObserver(GameObserver gameObserver) {
        game.addGameObserver(gameObserver);
    }

    public String getPlayerName(){
        return game.getPlayerName();
    }
}
