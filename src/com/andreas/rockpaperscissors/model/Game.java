package com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Game implements NetObserver {
    private String playerName;
    private List<Player> playerList = new ArrayList<>();
    private AppController appController = AppController.getInstance();
    private ArrayList<GameObserver> gameObservers = new ArrayList<>();
    private GameRound gameRound = null;
    private int totalScore = 0;


    public Game(String playerName) {
        this.playerName = playerName;
    }


    private void addPlayerIfNew(Player player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
            notifyPlayerJoinedTheGame(player.getName());
            appController.sendPlayerInfo();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    private void notifyPlayerJoinedTheGame(String newPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : playerList)
            playerNames.add(player.getName());
        for (GameObserver gameObserver : gameObservers) {
            gameObserver.playerJoinedTheGame(newPlayer);
            gameObserver.allPlayers(playerNames);
        }
    }

    private void notifyPlayerLeftTheGame(String lostPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : playerList)
            playerNames.add(player.getName());
        for (GameObserver gameObserver : gameObservers) {
            gameObserver.playerLeftTheGame(lostPlayer);
            gameObserver.allPlayers(playerNames);
        }
    }


    @Override
    public void playerInfo(String playerName) {
        Logger.log("Game got player info: " + playerName);
        Player player = new Player(playerName);
        addPlayerIfNew(player);
    }

    @Override
    public void playerNotResponding(String playerName) {
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            if (player.getName().equals(playerName)) {
                playerList.remove(player);
                if (gameRound != null)
                    gameRound.removePlayer(playerName);
                notifyPlayerLeftTheGame(playerName);
                i--;
            }
        }

    }


    @Override
    public void playerPlaysCommand(String playerName, PlayCommand playCommand) {
        if (gameRound == null)
            gameRound = new GameRound(playerList, this);
        gameRound.playerPlaysCommand(playerName, playCommand);
    }

    @Override
    public void chatMessage(String message) {
        for (GameObserver gameObserver : gameObservers)
            gameObserver.chatMessage(message);
    }

    public void roundCompleted(GameRound gameRound) {
        if (gameRound.isDraw())
            draw();
        else if (gameRound.isWonBy(playerName))
            victory(gameRound.scoreForWinner(playerName));
        else
            loss();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                newRound();
            }
        }, 3000);
    }

    private void newRound() {
        for (GameObserver gameObserver : gameObservers)
            gameObserver.newRound(totalScore);
        this.gameRound = null;
    }

    private void draw() {
        for (GameObserver gameObserver : gameObservers)
            gameObserver.draw();
    }

    private void loss() {
        for (GameObserver gameObserver : gameObservers)
            gameObserver.loss();
    }

    private void victory(int roundScore) {
        totalScore += roundScore;
        for (GameObserver gameObserver : gameObservers)
            gameObserver.victory(roundScore, totalScore);
    }

    public void addGameObserver(GameObserver gameObserver) {
        gameObservers.add(gameObserver);
    }
}
