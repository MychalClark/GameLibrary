package edu.ranken.mychal_clark.gamelibrary.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.data.Game;

public class GameListModel extends ViewModel {

    //make live data
    private static final String LOG_TAG = "MovieListViewModel";

    private FirebaseFirestore db;
    private MutableLiveData<List<Game>> games;
    private ListenerRegistration gamesRegistration;


    public GameListModel() {
        db = FirebaseFirestore.getInstance();
        // add  live data
        games = new MutableLiveData<>(null);

        //get game collection
        gamesRegistration =
            db.collection("games")
                .orderBy("releaseYear")
                .addSnapshotListener(( query, error) -> {
            if (error != null) {
                // show error...
            } else {
                List<Game> newGames =
                    query != null ? query.toObjects(Game.class) : null;

                games.postValue(newGames);
                // show games...
            }
        });
    }

    @Override
    protected void onCleared() {
        if (gamesRegistration != null) {
            gamesRegistration.remove();
        }
        super.onCleared();
    }

    //return data
    public LiveData<List<Game>> getGames() { return games; }
}
