package com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.net.PlayCommand;

public interface GamePlayObserver {
    void playerPlaysCommand(String playerName, PlayCommand playCommand);
}
