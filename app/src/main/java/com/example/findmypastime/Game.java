package com.example.findmypastime;

public class Game {

    private String gameName;



    private String gameId;

    public Game() { }

    public Game(String gameId, String gameName) {
        this.gameName = gameName;
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

}


