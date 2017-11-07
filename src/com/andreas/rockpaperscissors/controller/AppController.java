package com.andreas.rockpaperscissors.controller;

import com.andreas.rockpaperscissors.model.*;
import com.andreas.rockpaperscissors.util.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

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

    public void createNewGame(String playerName, int port, CompletionHandler<Void, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            game = new Game(playerName);
            netDelegate = new NetDelegate(playerName);
            try {
                netDelegate.createServerSocket(port);
                netDelegate.addNetObserver(game);
                completionHandler.completed(null, null);
            } catch (IOException e) {
                completionHandler.failed(e, null);
            }
        });
    }


    public void connectTo(String host, int port, CompletionHandler<Void, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                netDelegate.connectTo(host, port);
                sendPlayerInfo(new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        completionHandler.completed(null, null);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        completionHandler.failed(exc, null);
                    }
                });
            } catch (IOException e) {
                completionHandler.failed(e, null);
            }
        });
    }

    public void getLocalHost(CompletionHandler<String, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            String localHost = null;
            try {
                localHost = netDelegate.getLocalHost();
            } catch (UnknownHostException e) {
                completionHandler.failed(e, null);
            }
            String tokens[] = localHost.split("/");
            if (tokens.length < 1)
                completionHandler.failed(new Exception(), null);
            completionHandler.completed(tokens[tokens.length - 1], null);
        });
    }

    public void getLocalPort(CompletionHandler<Integer, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            int port = netDelegate.getLocalPort();
            completionHandler.completed(port, null);
        });
    }

    public void sendPlayerInfo(CompletionHandler<Void, Void> nullableCompletionHandler) {
        CompletableFuture.runAsync(() -> {
            String playerName = game.getPlayerName();
            Message message = new Message(MessageType.PLAYER_INFO).setContent(playerName);
            try {
                netDelegate.sendMessage(message.setSenderName(game.getPlayerName()));
                if (nullableCompletionHandler != null)
                    nullableCompletionHandler.completed(null, null);
            } catch (IOException e) {
                if (nullableCompletionHandler != null)
                    nullableCompletionHandler.failed(e, null);
            }
        });
    }

    public void sendChatMessage(String messageContent, CompletionHandler<Void, Void> completionHandler) {
        String playerName = game.getPlayerName();
        Message message = new Message(MessageType.CHAT).setContent("[" + playerName + "] " + messageContent);
        try {
            netDelegate.sendMessage(message.setSenderName(game.getPlayerName()));
            completionHandler.completed(null, null);
        } catch (IOException e) {
            completionHandler.failed(e, null);
        }
    }

    public void sendPlayRock(CompletionHandler<Void, Void> completionHandler) {
        sendPlay(PlayCommand.ROCK, completionHandler);
    }

    public void sendPlayPaper(CompletionHandler<Void, Void> completionHandler) {
        sendPlay(PlayCommand.PAPER, completionHandler);
    }

    public void sendPlayScissors(CompletionHandler<Void, Void> completionHandler) {
        sendPlay(PlayCommand.SCISSORS, completionHandler);
    }

    private void sendPlay(PlayCommand playCommand, CompletionHandler<Void, Void> completionHandler) {
        Message message = new Message(MessageType.PLAY)
                .setSenderName(game.getPlayerName())
                .setPlayCommand(playCommand);
        try {
            netDelegate.sendMessage(message.setSenderName(game.getPlayerName()));
            completionHandler.completed(null, null);
        } catch (IOException e) {
            completionHandler.failed(e, null);
        }
    }

    public void addGameObserver(GameObserver gameObserver) {
        CompletableFuture.runAsync(() -> {
            game.addGameObserver(gameObserver);
        });
    }

    public void getPlayerName(CompletionHandler<String, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            String result = game.getPlayerName();
            completionHandler.completed(result, null);
        });
    }
}
