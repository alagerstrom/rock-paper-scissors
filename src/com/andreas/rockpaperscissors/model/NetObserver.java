package com.andreas.rockpaperscissors.model;


public interface NetObserver {
    void playerInfo(String playerName);
    void playerNotResponding(String playerName);
    void playerPlaysCommand(String playerName, PlayCommand playCommand);
    void chatMessage(String message);
}
