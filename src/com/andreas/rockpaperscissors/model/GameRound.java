package com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.util.Logger;

import java.util.*;

public class GameRound {

    private List<String> players = new ArrayList<>();
    private Map<String, PlayCommand> playCommandMap = new HashMap<>();
    private Game game;

    public GameRound(List<Player> playerList, Game game) {
        for (Player player : playerList)
            players.add(player.getName());
        this.game = game;
    }


    public void playerPlaysCommand(String playerName, PlayCommand playCommand) {
        playCommandMap.put(playerName, playCommand);
        Logger.log("Checking if round is complete...");
        tellGameIfRoundCompleted();
    }

    private void tellGameIfRoundCompleted() {
        if (isRoundComplete())
            game.roundCompleted(this);
    }

    private boolean isRoundComplete() {
        boolean result = true;
        for (String playerName : players) {
            if (!playCommandMap.containsKey(playerName)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public void removePlayer(String player) {
        players.remove(player);
        tellGameIfRoundCompleted();
    }

    public PlayCommand getPlayCommand(String playerName) {
        return playCommandMap.get(playerName);
    }

    public boolean isDraw() {
        return playCommandMap.containsValue(PlayCommand.ROCK) &&
                playCommandMap.containsValue(PlayCommand.SCISSORS) &&
                playCommandMap.containsValue(PlayCommand.PAPER)
                || containsSingleValue(playCommandMap);
    }

    private boolean containsSingleValue(Map<String, PlayCommand> playCommandMap) {
        List<PlayCommand> commands = new ArrayList<>();
        commands.addAll(playCommandMap.values());
        if (commands.size() < 2)
            return true;
        return count(commands.get(0)) == commands.size();
    }

    public int count(PlayCommand playCommand) {
        List<PlayCommand> commands = new ArrayList<>();
        commands.addAll(playCommandMap.values());
        return Collections.frequency(commands, playCommand);
    }

    public int scoreForWinner(String winner) {
        PlayCommand playCommand = getPlayCommand(winner);
        switch (playCommand) {
            case ROCK:
                return count(PlayCommand.SCISSORS);
            case PAPER:
                return count(PlayCommand.ROCK);
            case SCISSORS:
                return count(PlayCommand.PAPER);
        }
        return 0;
    }

    public boolean isWonBy(String playerName) {
        PlayCommand playCommand = playCommandMap.get(playerName);
        switch (playCommand) {
            case ROCK:
                if (count(PlayCommand.PAPER) == 0)
                    return !isDraw();
                break;
            case PAPER:
                if (count(PlayCommand.SCISSORS) == 0)
                    return !isDraw();
                break;
            case SCISSORS:
                if (count(PlayCommand.ROCK) == 0)
                    return !isDraw();
        }
        return false;
    }
}
