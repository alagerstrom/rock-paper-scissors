package com.andreas.rockpaperscissors.controller;

import com.andreas.rockpaperscissors.model.*;
import com.andreas.rockpaperscissors.net.*;
import com.andreas.rockpaperscissors.util.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class AppController {
    private final static AppController instance = new AppController();


    private Game game;
    private final NetHandler netHandler = NetHandler.getInstance();

    private AppController() {
        Logger.log("AppController created");
    }

    public static AppController getInstance() {
        return instance;
    }

    public void createNewGame(String playerName) {
        Logger.log("AppController starting new game");
        game = new Game(playerName);
        netHandler.addGamePlayObserver(game);
        netHandler.startAccepting();
        netHandler.startSendingHeartbeats();
    }


    public void connectTo(String host, int port) throws IOException {
        Logger.log("I should connectTo " + host + ", " + port);
        netHandler.connectTo(host, port);
    }

    public void addPlayerInfoObserver(PlayerInfoObserver playerInfoObserver) {
        netHandler.addPlayerInfoObserver(playerInfoObserver);
    }

    public void getLocalHost(Consumer consumer) {
        consumer.accept("Getting IP...");
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String localHost = netHandler.getLocalHost().toString();
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
        return netHandler.getLocalPort();
    }

    public void setPortToUse(int port) throws IOException {
        netHandler.createServerSocket(port);
    }

    public void addChatObserver(ChatObserver chatObserver) {
        netHandler.addChatObserver(chatObserver);
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
        SendMessageService sendMessageService = new SendMessageService(message.setSenderName(game.getPlayerName()));
        sendMessageService.start();
    }

    public void addPlayersObserver(PlayersObserver playersObserver) {
        game.addPlayersObserver(playersObserver);

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

    public void addRoundObserver(RoundObserver roundObserver) {
        game.addRoundObserver(roundObserver);
    }

    public String getPlayerName(){
        return game.getPlayerName();
    }
}
