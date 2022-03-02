package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;
import java.util.Map;

public class Game {

@DocumentId
public String id;
    public String name;
    public String description;
    public Integer releaseYear;
    public String gameImage;
    public Map<String, Boolean> consoles;
    public Date releaseDate;


    public Game() {}

//    public Game(String name, String description, Integer releaseYear, String gameImage, String id , Map<String,Boolean> consoles) {
//        this.name = name;
//        this.description = description;
//        this.releaseYear = releaseYear;
//        this.gameImage = gameImage;
//        this.id = id;
//        this.consoles = consoles;
//    }



    @NonNull
    @Override
    public String toString() {
        return "Game {" + id + ", " + name + "}";
    }
}
