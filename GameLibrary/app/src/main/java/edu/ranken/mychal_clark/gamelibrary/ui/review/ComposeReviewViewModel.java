package edu.ranken.mychal_clark.gamelibrary.ui.review;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Game;

public class ComposeReviewViewModel extends ViewModel {
    private static final String LOG_TAG = ComposeReviewViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;
    private ListenerRegistration gameRegistration;
    private String userId;  // FIXME: make this a local variable

    // live data
    private final MutableLiveData<String> gameName;
    private final MutableLiveData<Integer> errorMessage;  // FIXME: translate
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<Boolean> finished;
    private final MutableLiveData<String> currentUserId;


    public ComposeReviewViewModel() {
        db = FirebaseFirestore.getInstance();


        //live Data
        gameName = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        finished = new MutableLiveData<>(false);
        currentUserId = new MutableLiveData<>(null);

        currentUserId.postValue(FirebaseAuth.getInstance().getCurrentUser().toString());
    }

    @Override
    protected void onCleared() {
        if (gameRegistration != null) {
            gameRegistration.remove();
        }
        super.onCleared();
    }

    // Get Live Data
    public LiveData<String> getGameName() {
        return gameName;
    }

    public LiveData<Integer> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getFinished() {
        return finished;
    }
    public LiveData<String> getUserId(){return currentUserId;}


    //get game
    public void fetchGame(String gameId) {

        Log.i(LOG_TAG, "game id y" + gameId);

        if (gameRegistration != null) {
            gameRegistration.remove();
        }

        if (gameId == null) {
            this.gameName.postValue(null);
            this.errorMessage.postValue(R.string.writeReviewError);
            this.snackbarMessage.postValue(R.string.writeReviewError);
        } else {
            gameRegistration =
                db.collection("games")
                    .document(gameId)
                    .addSnapshotListener((document, error) -> {
                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting game.", error);
                            this.errorMessage.postValue(R.string.errorGettingGame);
                            this.snackbarMessage.postValue(R.string.errorGettingGame);
                        } else if (document != null && document.exists()) {
                            Game game = document.toObject(Game.class);
                            this.gameName.postValue(game.name);
                            this.errorMessage.postValue(null);
                            this.snackbarMessage.postValue(R.string.noGameFound);
                        } else {
                            this.gameName.postValue(null);
                            this.errorMessage.postValue(R.string.noExsistingGame);
                            this.snackbarMessage.postValue(R.string.noExsistingGame);
                        }
                    });
        }

    }


    public void publishReview(String gameId, String reviewText) {
        /* validate reviewText and add review to database */
        // FIXME: tell the user when the text is empty
        if (reviewText != null) {
            //get current user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // FIXME: tell the user when they are not logged in
            if (user != null) {
                if(!reviewText.equals("")) {
                    userId = user.getUid();

                    // FIXME: include the profile photo, if available
                    Log.i(LOG_TAG, "Creating Review");
                    Map<String, Object> newReview = new HashMap<>();
                    newReview.put("displayName", user.getDisplayName());
                    newReview.put("userId", userId);
                    newReview.put("date", new Date());
                    newReview.put("reviewText", reviewText);
                    newReview.put("gameId", gameId);

                    // FIXME: activity is finished, before the review is actually published.
                    // FIXME: handle errors with publishing the review
                    db.collection("reviews").document(userId + ";" + gameId).set(newReview);
                    finished.postValue(true);
                }
                else{
                    errorMessage.postValue(R.string.addText);
                }
            }
        }



    }

    public void deleteReview(String reviewId){

        db.collection("reviews").document(reviewId).delete();
        Log.i(LOG_TAG, "document deleted");
        this.snackbarMessage.postValue(R.string.commentDeleted);


    }


}
