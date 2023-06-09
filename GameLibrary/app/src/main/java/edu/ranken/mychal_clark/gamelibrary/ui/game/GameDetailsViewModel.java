package edu.ranken.mychal_clark.gamelibrary.ui.game;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Game;
import edu.ranken.mychal_clark.gamelibrary.data.Review;
import edu.ranken.mychal_clark.gamelibrary.ebay.AuthAPI;
import edu.ranken.mychal_clark.gamelibrary.ebay.AuthEnvironment;
import edu.ranken.mychal_clark.gamelibrary.ebay.EbayBrowseAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameDetailsViewModel extends ViewModel {
    private static final String LOG_TAG = GameDetailsViewModel.class.getSimpleName();

    // firebase
    private final FirebaseFirestore db;
    private final AuthAPI authAPI;
    private final EbayBrowseAPI browseAPI;

    private String authToken;
    private String gameId;
    private String gameQuery;
    private String filterConsoleId = null;
    private String sort;
    private String console;
    private String gameMin;
    private String gameMax;
    private String filter;

    private ListenerRegistration gameRegistration;
    private ListenerRegistration reviewRegistration;

    // live data
    private final MutableLiveData<Game> game;
    private final MutableLiveData<Integer> gameError;
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<List<Review>> reviews;
    private final MutableLiveData<EbayBrowseAPI.SearchResponse> searchResponse;

    public GameDetailsViewModel() {
        db = FirebaseFirestore.getInstance();
        authAPI = new AuthAPI(AuthEnvironment.PRODUCTION, "MychalCl-gamelibr-PRD-9dc58052e-94b770a0", "PRD-dc58052ebc99-d273-45fa-a91b-96ef");
        browseAPI = new EbayBrowseAPI(AuthEnvironment.PRODUCTION);


        //Live Data
        game = new MutableLiveData<>(null);
        gameError = new MutableLiveData<>(R.string.noGameSelected);
        snackbarMessage = new MutableLiveData<>(null);
        reviews = new MutableLiveData<>(null);
        searchResponse = new MutableLiveData<>(null);

        //Get Auth
        authAPI.authenticateAsync(
            (authResponse) -> {
                // Authenticated.
                Log.i(LOG_TAG, "Authenticated.");
                // save auth token for later
                this.authToken = authResponse.access_token;
                search("");

            },
            (ex) -> {
                // Auth Error.
                // show error message
                Log.e(LOG_TAG, "Auth Error.", ex);
            }
        );

    }

    @Override
    protected void onCleared() {
        if (gameRegistration != null) {
            gameRegistration.remove();
        }
        if (reviewRegistration != null) {
            reviewRegistration.remove();
        }
        super.onCleared();
    }

    public LiveData<EbayBrowseAPI.SearchResponse> getSearchResponse() {
        return searchResponse;
    }

    public String getGameId() {
        return gameId;
    }

    public LiveData<Game> getGame() {
        return game;
    }

    // FIXME: must be observed and displayed to user
    public LiveData<Integer> getGameError() {
        return gameError;
    }


    public LiveData<Integer> getSnackbarMessage() {
        return snackbarMessage;
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    // remove snackbar message
    public void clearSnackbar() { snackbarMessage.postValue(null); }

    //Get the Game
    public void fetchGame(String gameId) {
        this.gameId = gameId;

        if (gameRegistration != null) {
            gameRegistration.remove();
        }
        if (reviewRegistration != null) {
            reviewRegistration.remove();
        }

        // get reviews

        if (gameId == null) {
            this.game.postValue(null);
            this.gameError.postValue(R.string.noGameSelected);
            this.snackbarMessage.postValue(R.string.noGameSelected);
        } else {

            Log.i(LOG_TAG, "game found. about to register.");
            gameRegistration =
                db.collection("games")
                    .document(gameId)
                    .addSnapshotListener((document, error) -> {
                        if (error != null) {
                            Log.e(LOG_TAG, "Error getting game.", error);
                            this.gameError.postValue(R.string.errorGettingGame);
                            this.snackbarMessage.postValue(R.string.errorGettingGame);
                        } else if (document != null && document.exists()) {
                            Game game = document.toObject(Game.class);
                            this.game.postValue(game);
                            this.gameError.postValue(null);
                            this.snackbarMessage.postValue(R.string.gameUpdated);

                            //For Ebay Price
                            gameQuery = document.getString("ebay.query");
                            if(gameQuery == null){gameQuery = document.getString("name");}

                            gameMin = document.getString("ebay.min");

                            gameMax = document.getString("ebay.max");

                            if(gameMin != null && gameMax != null){
                                filter =  "price:["+ gameMin + ".." + gameMax + "],priceCurrency:USD";
                            }else{filter = "";}




                            Log.i(LOG_TAG, "gamequery = " + gameQuery);

                            search("");
                        } else {
                            this.game.postValue(null);
                            this.gameError.postValue(R.string.noExsistingGame);
                            this.snackbarMessage.postValue(R.string.noExsistingGame);
                        }

                    });

            reviewRegistration = db.collection("reviews")
                .whereEqualTo("gameId", gameId)
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting reviews.", error);
                        snackbarMessage.postValue(R.string.errorGettingReviews);
                    } else {
                        List<Review> newReviews =
                            querySnapshot != null ? querySnapshot.toObjects(Review.class) : null;

                        reviews.postValue(newReviews);

                        snackbarMessage.postValue(R.string.reviewsUpdate);
                        // show games...
                    }
                });
        }



    }

    public void search (String console) {
        this.console = console;


        if(authToken != null && gameQuery != null) {
            browseAPI.searchAsync(
                authToken, gameQuery, 100, 1249, "newlyListed", filter,
                new Callback<EbayBrowseAPI.SearchResponse>() {
                    @Override
                    public void onResponse(Call<EbayBrowseAPI.SearchResponse> call, Response<EbayBrowseAPI.SearchResponse> response) {

                        if (response.isSuccessful()) {
                            Log.i(LOG_TAG, "search request successful");
                            searchResponse.postValue(response.body()); // Status code 200-299
                        } else {
                            Log.e(LOG_TAG, "search request unsuccessful");  // Status code 400-599


                        }

                    }

                    @Override
                    public void onFailure(Call<EbayBrowseAPI.SearchResponse> call, Throwable t) {
                        Log.e(LOG_TAG, "network error", t);
                        snackbarMessage.postValue(R.string.networkError);
                    }
                });
        }
    }


}
