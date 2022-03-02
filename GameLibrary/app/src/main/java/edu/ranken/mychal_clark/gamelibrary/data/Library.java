package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public class Library {
    @DocumentId
    public String id;
    public String gameId;
    public String username;

    public GameSummary game;

    public Library(){}

    @NonNull
    @Override
    public String toString() {
        return "Library {id=" + id + ", username=" + username + "}";
    }
}
