package com.andreas.rockpaperscissors.model;

public class Player {
    private String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player))
            return false;
        Player otherPlayer = (Player) obj;
        return otherPlayer.name.equals(name);
    }
}
