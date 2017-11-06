package com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Game implements PlayerInfoObserver, GamePlayObserver {
    private String playerName;
    private List<Player> playerList = new ArrayList<>();
    private AppController appController = AppController.getInstance();
    private ArrayList<PlayersObserver> playersObservers = new ArrayList<>();
    private ArrayList<RoundObserver> roundObservers = new ArrayList<>();
    private Round round = null;
    int totalPoints = 0;


    public Game(String playerName) {
        this.playerName = playerName;
    }


    private void addPlayerIfNew(Player player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
            notifyPlayersObserversNewPlayer(player.getName());
            appController.sendPlayerInfo();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    private void notifyPlayersObserversNewPlayer(String newPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : playerList)
            playerNames.add(player.getName());
        for (PlayersObserver playersObserver : playersObservers) {
            playersObserver.newPlayer(newPlayer);
            playersObserver.allPlayers(playerNames);
        }
    }

    private void notifyPlayersObserversPlayerLeft(String lostPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : playerList)
            playerNames.add(player.getName());
        for (PlayersObserver playersObserver : playersObservers) {
            playersObserver.playerLeft(lostPlayer);
            playersObserver.allPlayers(playerNames);
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
                notifyPlayersObserversPlayerLeft(playerName);
                i--;
            }
        }

    }

    public void addPlayersObserver(PlayersObserver playersObserver) {
        this.playersObservers.add(playersObserver);
    }

    @Override
    public void playerPlaysCommand(String playerName, PlayCommand playCommand) {
        if (round == null)
            round = new Round(playerList, this);
        round.playerPlaysCommand(playerName, playCommand);
    }

    public void roundCompleted(Round round) {
        if (round.isDraw())
            draw();
        else if (round.isWonBy(getPlayerName()))
            victory(1);
        else
            loss();
        this.round = null;
    }

    public void draw() {
        for (RoundObserver roundObserver : roundObservers)
            roundObserver.draw();
    }

    public void loss() {
        for (RoundObserver roundObserver : roundObservers)
            roundObserver.loss();
    }

    public void victory(int roundScore) {
        for (RoundObserver roundObserver : roundObservers)
            roundObserver.victory(roundScore, totalPoints);
    }

    public void addRoundObserver(RoundObserver roundObserver) {
        roundObservers.add(roundObserver);
    }
}
