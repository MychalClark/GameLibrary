package edu.ranken.mychal_clark.gamelibrary.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.data.Game;

public class GameListModel extends ViewModel {

    //make live data
    private static final String LOG_TAG = "MovieListViewModel";

    private FirebaseFirestore db;

    private ListenerRegistration gamesRegistration;
    private ListenerRegistration gamesLibraryRegistration;
    private ListenerRegistration consoleRegistration;

    private final String username = "GrinGrown394";
    private String filterByConsole = null;

    //Live Data
    private final MutableLiveData<List<Game>> games;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Consoles>> consoles;






    public GameListModel() {
        db = FirebaseFirestore.getInstance();
        // add  live data
        consoles = new MutableLiveData<>(null);
        games = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

        queryGames();

        gamesLibraryRegistration = db.collection("games")
            .whereEqualTo("user", username)

            .addSnapshotListener(( query, error) -> {
                if (error != null) {
                    // show error...
                } else {


                }
            });

//observe genres collection
        consoleRegistration =
            db.collection("consoles").addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error)->{

                if(error != null){
                    Log.e(LOG_TAG, "Error getting consoles.", error);

                }else{
                    List<Consoles> newConsoles = querySnapshot.toObjects(Consoles.class);
                    consoles.postValue(newConsoles);
                }
            });
    }

    @Override
    protected void onCleared() {
        if (gamesRegistration != null) {
            gamesRegistration.remove();
        }
        if (gamesLibraryRegistration != null) {
            gamesLibraryRegistration.remove();
        }
        super.onCleared();
    }

    //return data
    public LiveData<List<Game>> getGames() { return games; }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }
    public LiveData<List<Consoles>> getConsoles() { return consoles; }

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    public void filterGamesByConsole(String consoleId){
        this.filterConsoleId = consoleId;
        queryGames();
    }

    private void queryGames(){

        if (gamesRegistration !=null){
            gamesRegistration.remove();
        }

        Query query = db.collection("games");
        if(filterConsoleId != null){
            query = query.whereEqualTo("consoles." + filterConsoleId, true);
        }
        //get game collection
        gamesRegistration =
                query.addSnapshotListener(( QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting games.", error);
                        errorMessage.postValue(error.getMessage());
                        snackbarMessage.postValue("Error getting games.");
                    } else {
                        List<Game> newGames =
                            querySnapshot != null ? querySnapshot.toObjects(Game.class) : null;

                        games.postValue(newGames);
                        errorMessage.postValue(null);
                        snackbarMessage.postValue("Games Updated.");
                        // show games...
                    }
                });

    }






    private void chooseList(String gameId){

        db.collection("gameLibrary").document(username+ ";" + gameId)
        .addSnapshotListener(( query, error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting library.", error);
                errorMessage.postValue(error.getMessage());
                snackbarMessage.postValue("Error getting library.");
                // show error...
            }
            else {


            }
        });


    }
}
