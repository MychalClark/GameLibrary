package edu.ranken.mychal_clark.gamelibrary.data;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class User {
    @DocumentId
    String id;
    String userName;
    Date creationDate;
}
