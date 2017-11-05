package com.andreas.rockpaperscissors.net;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.model.GamePlayObserver;
import com.andreas.rockpaperscissors.model.PlayerInfoObserver;
import com.andreas.rockpaperscissors.util.Logger;
import com.andreas.rockpaperscissors.model.ChatObserver;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class NetHandler {
    private final static NetHandler instance = new NetHandler();

    private ServerSocket serverSocket;
    private final List<Connection> connections = new ArrayList<>();
    private List<ChatObserver> chatObservers = new ArrayList<>();
    private List<PlayerInfoObserver> playerInfoObservers = new ArrayList<>();
    private ArrayList<String> knownPeers = new ArrayList<>();
    private HashMap<String, Long> lastTimeHeardFromPeer = new HashMap<>();
    private HashMap<String, Integer> seenMessages = new HashMap<>();
    private ArrayList<GamePlayObserver> gamePlayObservers = new ArrayList<>();
    private int myMessageNumber;


    public static NetHandler getInstance() {
        return instance;
    }

    private class HeartbeatSender extends TimerTask{
        @Override
        public void run() {
            try {
                sendHeartbeat();
            } catch (IOException e) {
                Logger.log("Failed to send heartbeat");
            }
        }
    }
    private class HeartbeatCounter extends TimerTask{
        @Override
        public void run() {
            checkIfHeartbeatsHaveBeenReceived();
        }
    }

    private void checkIfHeartbeatsHaveBeenReceived() {
        for (int i = 0; i < knownPeers.size(); i++){
            String peer = knownPeers.get(i);
            long now = System.currentTimeMillis();
            long lastHeartbeat = lastTimeHeardFromPeer.get(peer);
            if (now - lastHeartbeat > 3000){
                for (PlayerInfoObserver playerInfoObserver : playerInfoObservers)
                    playerInfoObserver.playerNotResponding(peer);
                seenMessages.remove(peer);
                lastTimeHeardFromPeer.remove(peer);
                knownPeers.remove(peer);
                i--;
            }
        }
    }

    private NetHandler() {

    }
    public void startSendingHeartbeats(){
        Timer timer = new Timer(true);
        timer.schedule(new HeartbeatSender(), 0, 1000);
        timer.schedule(new HeartbeatCounter(), 0, 500);
    }
    private void sendHeartbeat() throws IOException {
        sendMessage(new Message(MessageType.HEARTBEAT).setSenderName(AppController.getInstance().getPlayerName()));
    }

    public void addPlayerInfoObserver(PlayerInfoObserver playerInfoObserver) {
        this.playerInfoObservers.add(playerInfoObserver);
    }

    public InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
    public int getLocalPort(){
        return serverSocket.getLocalPort();
    }

    public void removeConnection(Connection connection){
        synchronized (connections){
            connections.remove(connection);
        }
    }

    void addConnection(Socket socket) throws IOException {
        connections.add(new Connection(socket));
        Logger.log("Added connection.");
    }

    public void connectTo(String host, int port) throws IOException {
        Logger.log("NetHandler should connectButtonClicked to " + host + ", " + port);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        synchronized (connections){
            connections.add(new Connection(socket));
        }
        AppController.getInstance().sendPlayerInfo();
    }


    public void createServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.log("Listening on port " + port);
    }

    public void startAccepting() {
        new AcceptService(serverSocket).start();
    }

    private void broadcast(Message message) throws IOException {
        handleIncomingMessage(message);
        synchronized (connections) {
            for (Connection connection : connections)
                connection.send(message);
        }
    }


    void handleIncomingMessage(Message message) throws IOException {
        if (!isNewMessage(message))
            return;
        broadcast(message);
        Logger.log("Received message " + message.getType() + ", "
                + message.getSenderName() + ", "
                + message.getNumber() + ", "
                + message.getContent());
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
            case HEARTBEAT:
                long now = System.currentTimeMillis();
                lastTimeHeardFromPeer.put(message.getSenderName(), now);
                if (!knownPeers.contains(message.getSenderName()))
                    knownPeers.add(message.getSenderName());
                break;
            default:
                Logger.log("Received unknown message");
                break;
        }
    }

    private boolean isNewMessage(Message message) {
        boolean isNew = false;
        if (seenMessages.containsKey(message.getSenderName())){
            int oldNumber = seenMessages.get(message.getSenderName());
            if (oldNumber < message.getNumber())
                isNew = true;
        }else{
            isNew = true;
        }
        if (isNew)
            seenMessages.put(message.getSenderName(), message.getNumber());
        return isNew;
    }

    private void notifyPlayerObservers(String playerName) {
        for (PlayerInfoObserver playerInfoObserver : playerInfoObservers){
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

    public void sendMessage(Message message) throws IOException {
        broadcast(message.setNumber(myMessageNumber++));
    }

    public void addGamePlayObserver(GamePlayObserver gamePlayObserver){
        gamePlayObservers.add(gamePlayObserver);
    }
    private void notifyGamePlayObservers(String playerName, PlayCommand playCommand){
        for (GamePlayObserver gamePlayObserver : gamePlayObservers)
            gamePlayObserver.playerPlaysCommand(playerName, playCommand);
    }
}
