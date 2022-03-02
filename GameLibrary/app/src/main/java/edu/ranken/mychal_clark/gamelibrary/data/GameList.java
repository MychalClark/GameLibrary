package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

public enum GameList {
    ALL_GAMES("All Games"),
    WISHLIST("Wishlist"),
    LIBRARY("Library"),
    LIBRARY_WISHLIST("Library and Wishlist");

    private final String name;

    private GameList(@NonNull String name)
    {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }
}
