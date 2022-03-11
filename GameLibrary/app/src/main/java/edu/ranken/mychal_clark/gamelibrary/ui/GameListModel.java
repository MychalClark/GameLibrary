package edu.ranken.mychal_clark.gamelibrary.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.data.Game;
import edu.ranken.mychal_clark.gamelibrary.data.GameList;
import edu.ranken.mychal_clark.gamelibrary.data.GameSummary;

public class GameListModel extends ViewModel {

    //make live data
    private static final String LOG_TAG = "GameListViewModel";

    private final FirebaseFirestore db;

    private ListenerRegistration gamesRegistration;
    private ListenerRegistration libraryRegistration;
    private ListenerRegistration wishlistRegistration;
    private ListenerRegistration consolesRegistration;

    private String userId = null;
    private String filterConsoleId = null;
    private GameList filterList = GameList.ALL_GAMES;

    //Live Data
    private final MutableLiveData<List<GameSummary>> games;

    private final MutableLiveData<List<Consoles>> consoles;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<String> errorMessage;


    public GameListModel() {
        db = FirebaseFirestore.getInstance();
        // add  live data
        consoles = new MutableLiveData<>(null);
        games = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);


        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            Log.i(LOG_TAG, "This is the users Id  " + userId);
        } else {
            userId = null;
        }
        //figures out if games is changing
        queryGames();

//observe consoles collection
        libraryRegistration =
            db.collection("userLibrary").whereEqualTo("userId", userId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    Log.e(LOG_TAG, "Error getting Library.", error);
                    snackbarMessage.postValue("Error getting Library.");
                } else if (querySnapshot != null) {
                    Log.i(LOG_TAG, "Library Updated.");

                }


            });


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

    @Override
    protected void onCleared() {
        if (gamesRegistration != null) {
            gamesRegistration.remove();
        }
        if (libraryRegistration != null) {
            libraryRegistration.remove();
        }
        if (wishlistRegistration != null) {
            wishlistRegistration.remove();
        }
        if (consolesRegistration != null) {
            consolesRegistration.remove();
        }
        super.onCleared();
    }

    //return data
    public LiveData<List<GameSummary>> getGames() {
        return games;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }

    public LiveData<List<Consoles>> getConsoles() {
        return consoles;
    }

    public String getFilterConsoleId() {
        return filterConsoleId;
    }

    public void filterGamesByList(GameList list) {
        this.filterList = list;
        queryGames();
    }

    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    public void filterGamesByConsole(String consoleId) {
        this.filterConsoleId = consoleId;
        queryGames();
    }

    private void queryGames() {

        if (gamesRegistration != null) {
            gamesRegistration.remove();
            Log.i(LOG_TAG, "Games removed");
        }

        //New method. Sort By: name and release year.


        Query query = null;
        switch (filterList) {
            default:
                throw new IllegalStateException("Unsupported Option");
            case ALL_GAMES:
                query = db.collection("games");
                break;
            case WISHLIST:
                query = db.collection("userWishlist")
                    .whereEqualTo("userId", userId);
                break;
            case LIBRARY:

               query = db.collection("userLibrary").whereEqualTo("userId", userId);



                Log.i(LOG_TAG, "Library Filtered");
                break;
            case LIBRARY_WISHLIST:
                db.collection("userLibrary")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("libraryValue", 1)
                    .whereEqualTo("wishlistValue", 1);
                break;
        }


        if (filterConsoleId != null) {
            if (filterList == GameList.ALL_GAMES) {
                query = query.whereEqualTo("consoles." + filterConsoleId, true);
            } else {
                if(filterList == GameList.LIBRARY){
                    query = query.whereEqualTo("consoles." + filterConsoleId, true);
                } else if(filterList == GameList.WISHLIST){
                    query = query.whereEqualTo("consoles." + filterConsoleId, true);}
                else if(filterList == GameList.LIBRARY_WISHLIST){
                    query = query.whereEqualTo("consoles." + filterConsoleId, true);}
                else{
                query = query.whereEqualTo("games.consoles." + filterConsoleId, true);}
            }
        }
        //get game collection
        gamesRegistration =
            query.addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    // show error...
                    Log.e(LOG_TAG, "Error getting games.", error);
                    errorMessage.postValue(error.getMessage());
                    snackbarMessage.postValue("Error getting games.");

                } else if (querySnapshot != null) {

                    Log.i(LOG_TAG, "Games update.");

                    ArrayList<GameSummary> newGameSummary = new ArrayList<>();
                    switch (filterList) {
                        default:
                            throw new IllegalStateException("Unsupported Option");
                        case ALL_GAMES:
                            List<Game> newGames = querySnapshot.toObjects(Game.class);
                            for (Game game : newGames) {
                                newGameSummary.add(new GameSummary(game));
                            }
                            break;
                        case LIBRARY:
                            List<Game> newGamesLibrary = querySnapshot.toObjects(Game.class);
                            for (Game game : newGamesLibrary) {
                                game.id = game.id.substring((game.id.indexOf(";")+1));
                                newGameSummary.add(new GameSummary(game));
                            }
                            break;
                        case WISHLIST:
                            List<Game> newGamesWishlist = querySnapshot.toObjects(Game.class);
                            for (Game game : newGamesWishlist) {
                                game.id = game.id.substring((game.id.indexOf(";")+1));
                                newGameSummary.add(new GameSummary(game));
                            }
                        case LIBRARY_WISHLIST:
                            break;
                    }
                    games.postValue(newGameSummary);

                    errorMessage.postValue(null);
                    snackbarMessage.postValue("Games Updated.");
                }

            });


    }

    public void wishlistChange(GameSummary game) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("userWishlist")
            .document(user.getUid() + ";" + game.id);

        docRef.get()
            .addOnCompleteListener((Task<DocumentSnapshot> task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.delete();
                        Log.i(LOG_TAG ,"Deleting document");
                    } else {
                        Map<String, Object> newGame = new HashMap<>();
                        newGame.put("userId", user.getUid());
                        newGame.put("gameId", game.id);
                        newGame.put("consoles", game.consoles);
                        newGame.put("description", game.description);
                        newGame.put("releaseYear", game.releaseYear);
                        newGame.put("gameImage", game.gameImage);
                        newGame.put("name", game.name);

                        Log.i(LOG_TAG, "Creating document");

                        docRef.set(newGame);
                    }

                }

            });

    }

    public void libraryChange(GameSummary game) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("userLibrary")
            .document(user.getUid() + ";" + game.id);

        docRef.get()
            .addOnCompleteListener((Task<DocumentSnapshot> task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.delete();
                        Log.i(LOG_TAG ,"Deleting document");
                    }
                    else {

                        Map<String, Object> newGame = new HashMap<>();
                        newGame.put("userId", user.getUid());
                        newGame.put("gameId", game.id);
                        newGame.put("consoles", game.consoles);
                        newGame.put("description", game.description);
                        newGame.put("releaseYear", game.releaseYear);
                        newGame.put("gameImage", game.gameImage);
                        newGame.put("name", game.name);

                        Log.i(LOG_TAG, "Creating document");

                        docRef.set(newGame);



                    }

                }

            });

    }

}
