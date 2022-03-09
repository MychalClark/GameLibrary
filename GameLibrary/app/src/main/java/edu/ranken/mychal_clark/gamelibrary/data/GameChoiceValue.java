package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

public class GameChoiceValue {
    public String gameId;
    public String wishlistUsername;
    public String libraryUsername;

    public GameChoiceValue(){}

    public GameChoiceValue(@NonNull String gameId, String wishlistUsername, String libraryUsername){
        this.gameId = gameId;
        this.wishlistUsername = wishlistUsername;
        this.libraryUsername = libraryUsername;
    }

    public GameChoiceValue(@NonNull Library library){
        this.gameId = library.gameId;
        this.libraryUsername = library.userId;
    }
    public GameChoiceValue(@NonNull WishList wishlist){
        this.gameId = wishlist.gameId;
        this.wishlistUsername = wishlist.userId;
    }
    @NonNull
    @Override
    public String toString() {
        return "GameChoiceValue {id=" + gameId + ", wishlistUsername=" + wishlistUsername + ", libraryUsername=" + libraryUsername + "}";
    }
}
