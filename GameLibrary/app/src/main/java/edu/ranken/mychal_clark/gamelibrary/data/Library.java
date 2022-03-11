package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

public class Library {

    public String gameId;
    public String userId;
    public String gameImage;
    public String name;

    public GameSummary game;

    public Library(){}

    public Library(String gameId, String userId, String gameImage, String name){
        this.gameId = gameId;
        this.userId = userId;
        this.gameImage = gameImage;
        this.name = name;

    }

    @NonNull
    @Override
    public String toString() {
        return "Library {id=" + gameId + ", userId=" + userId + "}";
    }
}
