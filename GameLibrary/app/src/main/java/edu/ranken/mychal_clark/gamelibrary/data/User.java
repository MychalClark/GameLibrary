package edu.ranken.mychal_clark.gamelibrary.data;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class User {

    @DocumentId
   public String id;
    public String displayName;
   public  Date lastLoggedIn;
   public String profilePictureUrl;

    User(){}

    User(String displayName, String id, String profilePictureUrl, Date lastLoggedIn){
        this.displayName = displayName;
        this.id = id;
        this.profilePictureUrl = profilePictureUrl;
        this.lastLoggedIn = lastLoggedIn;
    }

}
