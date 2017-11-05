package com.andreas.rockpaperscissors.model;

import java.util.List;

public interface PlayersObserver {
    void allPlayers(List<String> allPlayers);
    void newPlayer(String playerName);
    void playerLeft(String lostPlayer);
}
