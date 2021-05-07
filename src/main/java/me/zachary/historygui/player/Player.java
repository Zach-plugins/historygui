package me.zachary.historygui.player;

import java.util.UUID;

public class Player {
    private final String playerName;
    private final UUID uuid;

    public Player(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uuid = uuid;
    }

    public String getPlayerName(){
        return playerName;
    }

    public UUID getUUID(){
        return uuid;
    }
}
