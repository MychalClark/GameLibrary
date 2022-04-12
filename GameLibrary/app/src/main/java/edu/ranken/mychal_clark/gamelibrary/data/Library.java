package edu.ranken.mychal_clark.gamelibrary.data;

import com.google.firebase.firestore.DocumentId;

import java.util.Map;

public class Library {
    @DocumentId
    public String id;

    public String userId;
    public String gameId;
    public Map<String, Boolean> consoles;
    public String description;
    public Integer releaseYear;
    public String gameImage;
    public String name;
    public Map<String, Boolean> selectedConsoles;
}
