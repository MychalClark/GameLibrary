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

        public Map<String,Boolean> selectedConsoles;
        public Map<String,Map> languages;

        public GameSummary(){}

        public GameSummary(@NonNull Game game){
                this.id = game.id;
                this.name = game.name;
                this.description = game.description;
                this.releaseYear = game.releaseYear;
                this.gameImage = game.gameImage;
                this.consoles = game.consoles;
                this.selectedConsoles = null;
        }

        public GameSummary(@NonNull Library game){
                this.id = game.gameId;
                this.name = game.name;
                this.description = game.description;
                this.releaseYear = game.releaseYear;
                this.gameImage = game.gameImage;
                this.consoles = game.consoles;
                this.selectedConsoles = game.selectedConsoles;
        }

        public GameSummary(@NonNull WishList game){
                this.id = game.gameId;
                this.name = game.name;
                this.description = game.description;
                this.releaseYear = game.releaseYear;
                this.gameImage = game.gameImage;
                this.consoles = game.consoles;
                this.selectedConsoles = game.selectedConsoles;
        }

        @NonNull
        @Override
        public String toString() {
                return "GameSummary {" + id + ", " + name + "}";
        }
    }
