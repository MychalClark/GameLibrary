package edu.ranken.mychal_clark.gamelibrary.data;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;
import java.util.Map;

public class User {

    @DocumentId
   public String id;
    public String displayName;
   public  Date lastLoggedIn;
   public String profilePictureUrl;
   public Map<String, Boolean> preferredConsoles;

    User(){}

    User(String displayName, String id, String profilePictureUrl, Date lastLoggedIn, Map<String, Boolean> preferredConsoles){
        this.displayName = displayName;
        this.id = id;
        this.profilePictureUrl = profilePictureUrl;
        this.lastLoggedIn = lastLoggedIn;
        this.preferredConsoles = preferredConsoles;
    }

}
