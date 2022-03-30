package edu.ranken.mychal_clark.gamelibrary.ebay;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.data.Game;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EbayBrowseViewModel extends ViewModel {
    private final String LOG_TAG = EbayBrowseViewModel.class.getSimpleName();

    private final FirebaseFirestore db;
    private final AuthAPI authAPI;
    private final EbayBrowseAPI browseAPI;

    private String listingStatus;
    private String authToken;
    private String gameId;
    private String gameQuery;
    private String filterConsoleId = null;
    private String sort;
    private String console;
    private String gameMin;
    private String gameMax;
    private String filter;


    private ListenerRegistration consolesRegistration;


    //Live Data
    private final MutableLiveData<EbayBrowseAPI.SearchResponse> searchResponse;
    private final MutableLiveData<Game> game;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<List<Consoles>> consoles;


    public EbayBrowseViewModel() {
        db = FirebaseFirestore.getInstance();
        authAPI = new AuthAPI(AuthEnvironment.PRODUCTION, "PaulSmit-explorer-PRD-dba622b8c-288c89fc", "PRD-ba622b8c7a63-7c21-4cef-8a9f-55df");
        browseAPI = new EbayBrowseAPI(AuthEnvironment.PRODUCTION);

        //live Data
        consoles = new MutableLiveData<>(null);
        searchResponse = new MutableLiveData<>(null);
        game = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

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

        consolesRegistration =
            db.collection("consoles")
                .orderBy("name")
                .addSnapshotListener((@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {

                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting consoles.", error);

                    } else {
                        List<Consoles> newConsoles = querySnapshot.toObjects(Consoles.class);
                        consoles.postValue(newConsoles);
                    }
                });


    }

    public LiveData<EbayBrowseAPI.SearchResponse> getSearchResponse() {
        return searchResponse;
    }
    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }
    public LiveData<Game> getGame() {
        return game;
    }
    public LiveData<List<Consoles>> getConsoles() {
        return consoles;
    }
    public String getFilterConsoleId() {
        return filterConsoleId;
    }

    public void fetchGame(String gameId) {
        this.gameId = gameId;
         db.collection("games").document(gameId)
            .get()
             .addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot != null && documentSnapshot.exists()) {

                    gameQuery = documentSnapshot.getString("ebay.query");
                    if(gameQuery == null){gameQuery = documentSnapshot.getString("name");}

                    gameMin = documentSnapshot.getString("ebay.min");

                    gameMax = documentSnapshot.getString("ebay.max");

                    if(gameMin != null && gameMax != null){
                       filter =  "price:["+ gameMin + ".." + gameMax + "],priceCurrency:USD";
                    }else{filter = "";}




                    Log.i(LOG_TAG, "gamequery = " + gameQuery);

                    search("");
                }else{
                    Log.e(LOG_TAG, "Game not found.");
                }
         }).addOnFailureListener(error -> {
             Log.e(LOG_TAG, "Failed to query the database for game." ,error);

         });
    }
public void fetchListingStatus(String listing){
        this.listingStatus = listing;
        search(console);
}
    public void fetchSort(String sort){
        this.sort = sort;
        search(console);
    }
        public void search (String console) {
        this.console = console;


            if(authToken != null && gameQuery != null) {
                browseAPI.searchAsync(
                    authToken, gameQuery+" "+ console, 5, 1249, sort, filter,
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
                        }
                    });
            }
        }
    }
