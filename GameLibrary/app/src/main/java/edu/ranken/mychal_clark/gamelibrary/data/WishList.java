package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public class WishList
{
    @DocumentId
    public String id;
    public String gameId;
    public String userId;

    public GameSummary game;

    public WishList(){}

    @NonNull
    @Override
    public String toString() {
        return "WishList {id=" + id + ", value=" + userId + "}";
    }
}
