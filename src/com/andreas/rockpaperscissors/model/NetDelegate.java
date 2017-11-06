package com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.net.NetHandler;
import com.andreas.rockpaperscissors.net.SendMessageService;
import com.andreas.rockpaperscissors.util.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NetDelegate implements NetHandler.Delegate<Message> {

    private List<ChatObserver> chatObservers = new ArrayList<>();
    private List<PlayerInfoObserver> playerInfoObservers = new ArrayList<>();
    private ArrayList<GamePlayObserver> gamePlayObservers = new ArrayList<>();

    private NetHandler netHandler = NetHandler.getInstance();

    public NetDelegate() {
        netHandler.setDelegate(this);
    }
    public void start(){
        netHandler.start();
    }

    @Override
    public void onNewMessage(Message message) {
        switch (message.getType()) {
            case CHAT:
                notifyChatObservers(message.getContent());
                break;
            case PLAYER_INFO:
                notifyPlayerObservers(message.getContent());
                break;
            case PLAY:
                Logger.log("Received: " + message.getSenderName() + " plays " + message.getPlayCommand());
                notifyGamePlayObservers(message.getSenderName(), message.getPlayCommand());
                break;
        }
    }

    @Override
    public void peerNotResponding(String uniqueName) {
        for (PlayerInfoObserver playerInfoObserver : playerInfoObservers)
            playerInfoObserver.playerNotResponding(uniqueName);

    }

    private void notifyPlayerObservers(String playerName) {
        for (PlayerInfoObserver playerInfoObserver : playerInfoObservers) {
            playerInfoObserver.playerInfo(playerName);
        }
    }

    private void notifyChatObservers(String messageContent) {
        for (ChatObserver chatObserver : chatObservers)
            chatObserver.newMessage(messageContent);
    }

    public void addChatObserver(ChatObserver chatObserver) {
        this.chatObservers.add(chatObserver);
    }


    public void addPlayerInfoObserver(PlayerInfoObserver playerInfoObserver) {
        this.playerInfoObservers.add(playerInfoObserver);
    }

    public void addGamePlayObserver(GamePlayObserver gamePlayObserver) {
        gamePlayObservers.add(gamePlayObserver);
    }

    private void notifyGamePlayObservers(String playerName, PlayCommand playCommand) {
        for (GamePlayObserver gamePlayObserver : gamePlayObservers)
            gamePlayObserver.playerPlaysCommand(playerName, playCommand);
    }

    public void connectTo(String host, int port) throws IOException {
        netHandler.connectTo(host, port);
    }

    public void createServerSocket(int port) throws IOException {
        netHandler.createServerSocket(port);
    }

    public void sendMessageOnNewThread(Message message) {
        SendMessageService sendMessageService = new SendMessageService(message.setSenderName(AppController.getInstance().getPlayerName()));
        sendMessageService.start();
    }

    public String getLocalHost() throws UnknownHostException {
        return netHandler.getLocalHost().toString();
    }

    public int getLocalPort() {
        return netHandler.getLocalPort();
    }

    public void setPlayerName(String playerName) {
        netHandler.setUniqueName(playerName);
    }
}
