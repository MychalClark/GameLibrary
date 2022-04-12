package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Review {

    @DocumentId
    public String id;
    public String displayName;
    public String reviewText;
    public Date date;
    public String userId;

    public Review() {}

    public Review(String displayName, String reviewText, Date date, String userId){
        this.displayName = displayName;
        this.reviewText = reviewText;
        this.date = date;
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Review {" + id + ", " + displayName + "}";
    }
}




