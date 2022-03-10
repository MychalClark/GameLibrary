package edu.ranken.mychal_clark.gamelibrary.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.data.Game;
import edu.ranken.mychal_clark.gamelibrary.data.Review;

public class GameDetailsViewModel extends ViewModel {
    private static final String LOG_TAG = GameDetailsViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;
    private ListenerRegistration gameRegistration;
    private ListenerRegistration reviewRegistration;
    private String gameId;

    // live data
    private final MutableLiveData<Game> game;
    private final MutableLiveData<String> gameError;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<List<Review>> reviews;

    public GameDetailsViewModel() {
        db = FirebaseFirestore.getInstance();

        //Live Data
        game = new MutableLiveData<>(null);
        gameError = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        reviews = new MutableLiveData<>(null);

    }

    @Override
    protected void onCleared(){
        if (gameRegistration != null) {
            gameRegistration.remove();
        }
        super.onCleared();
    }

    public String getGameId() {
        return gameId;
    }

    public LiveData<Game> getGame() {
        return game;
    }

    public LiveData<String> getGameError() {
        return gameError;
    }

    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }

    public LiveData<List<Review>> getReviews(){return reviews;}

    //clears the snackbar
    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    //Get the Game
    public void fetchGame(String gameId) {
        this.gameId = gameId;

        if (gameRegistration != null) {
            gameRegistration.remove();
        }

        //get reviews
        reviewRegistration =
            db.collection("reviews")
                .whereEqualTo("gameId", gameId)
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    // show error...
                    Log.e(LOG_TAG, "Error getting reviews.", error);
                    snackbarMessage.postValue("Error getting Reviews.");
                } else {
                    List<Review> newReviews =
                        querySnapshot != null ? querySnapshot.toObjects(Review.class) : null;

                    reviews.postValue(newReviews);

                    snackbarMessage.postValue("Reviews Updated.");
                    // show games...
                }
            });

        if (gameId == null) {
            this.game.postValue(null);
            this.gameError.postValue("No Game selected.");
            this.snackbarMessage.postValue("No Game selected.");
        } else {
            gameRegistration =
                db.collection("games")
                    .document(gameId)
                    .addSnapshotListener((document, error) -> {
                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting game.", error);
                            this.gameError.postValue("Error getting game.");
                            this.snackbarMessage.postValue("Error getting game.");
                        } else if (document != null && document.exists()) {
                            Game game = document.toObject(Game.class);
                            this.game.postValue(game);
                            this.gameError.postValue(null);
                            this.snackbarMessage.postValue("Game updated.");
                        } else {
                            this.game.postValue(null);
                            this.gameError.postValue("Game does not exist.");
                            this.snackbarMessage.postValue("Game does not exist.");
                        }
                    });
        }

    }



}
