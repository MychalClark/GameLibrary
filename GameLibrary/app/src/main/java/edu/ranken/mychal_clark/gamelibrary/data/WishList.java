package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

public class WishList
{
    public String gameId;
    public String userId;
    public String gameImage;
    public String name;

    public GameSummary game;

    public WishList(){}

    public WishList(String gameId, String userId, String gameImage, String name){
        this.gameId = gameId;
        this.userId = userId;
        this.gameImage = gameImage;
        this.name = name;

    }

    @NonNull
    @Override
    public String toString() {
        return "Wishlist {id=" + gameId + ", userId=" + userId + "}";
    }
}
