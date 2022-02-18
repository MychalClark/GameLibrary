package edu.ranken.mychal_clark.gamelibrary.data;

public class Game {

    public String name;
    public String description;
    public Integer releaseYear;
    public String image;

    public Game() {}

    public Game(String name, String description, Integer releaseYear, String image) {
        this.name = name;
        this.description = description;
        this.releaseYear = releaseYear;
        this.image = image;
    }
}
