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

    private List<NetObserver> netObservers = new ArrayList<>();

    private NetHandler<Message> netHandler;
    private String uniqueName;

    public NetDelegate(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public void createServerSocket(int port) throws IOException {
        netHandler = new NetHandler<>(uniqueName, port, this);
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
                notifyPlayerPlaysCommand(message.getSenderName(), message.getPlayCommand());
                break;
        }
    }

    @Override
    public void peerNotResponding(String uniqueName) {
        for (NetObserver netObserver : netObservers)
            netObserver.playerNotResponding(uniqueName);

    }

    private void notifyPlayerObservers(String playerName) {
        for (NetObserver netObserver : netObservers) {
            netObserver.playerInfo(playerName);
        }
    }

    private void notifyChatObservers(String messageContent) {
        for (NetObserver netObserver : netObservers)
            netObserver.chatMessage(messageContent);
    }


    public void addNetObserver(NetObserver netObserver) {
        this.netObservers.add(netObserver);
    }

    private void notifyPlayerPlaysCommand(String playerName, PlayCommand playCommand) {
        for (NetObserver netObserver : netObservers)
            netObserver.playerPlaysCommand(playerName, playCommand);
    }

    public void connectTo(String host, int port) throws IOException {
        netHandler.connectTo(host, port);
    }



    public void sendMessageOnNewThread(Message message) {
        netHandler.sendMessage(message.setSenderName(AppController.getInstance().getPlayerName()));
    }

    public String getLocalHost() throws UnknownHostException {
        return netHandler.getLocalHost().toString();
    }

    public int getLocalPort() {
        return netHandler.getLocalPort();
    }

}
