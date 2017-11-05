package com.andreas.rockpaperscissors.model;


public interface PlayerInfoObserver {
    void playerInfo(String playerName);
    void playerNotResponding(String playerName);
}
