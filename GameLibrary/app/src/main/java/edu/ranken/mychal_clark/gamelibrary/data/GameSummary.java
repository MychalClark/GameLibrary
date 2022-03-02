package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

import java.util.Map;

public class GameSummary {
public String id;
        public String name;
        public String description;
        public Integer releaseYear;
        public String gameImage;
        public Map<String, Boolean> consoles;

        public GameSummary(){}

        public GameSummary(@NonNull Game game){
                this.id = game.id;
                this.name = game.name;
                this.description = game.description;
                this.releaseYear = game.releaseYear;
                this.gameImage = game.gameImage;
                this.consoles = game.consoles;
        }

        @NonNull
        @Override
        public String toString() {
                return "GameSummary {" + id + ", " + name + "}";
        }
    }
