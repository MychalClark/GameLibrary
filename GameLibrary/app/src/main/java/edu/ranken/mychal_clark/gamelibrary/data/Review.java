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

    public Review() {}

    public Review(String displayName, String reviewText, Date date){
        this.displayName = displayName;
        this.reviewText = reviewText;
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Review {" + id + ", " + displayName + "}";
    }
}




