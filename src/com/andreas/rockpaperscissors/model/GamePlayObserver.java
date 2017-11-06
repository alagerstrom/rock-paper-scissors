package com.andreas.rockpaperscissors.model;

public interface GamePlayObserver {
    void playerPlaysCommand(String playerName, PlayCommand playCommand);
}
