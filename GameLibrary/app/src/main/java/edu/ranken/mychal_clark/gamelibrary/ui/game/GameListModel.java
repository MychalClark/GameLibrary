package edu.ranken.mychal_clark.gamelibrary.ui.game;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.data.Game;
import edu.ranken.mychal_clark.gamelibrary.data.GameList;
import edu.ranken.mychal_clark.gamelibrary.data.GameSummary;
import edu.ranken.mychal_clark.gamelibrary.data.Library;
import edu.ranken.mychal_clark.gamelibrary.data.WishList;

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
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<List<Library>> library;
    private final MutableLiveData<List<WishList>> wishlist;
    private final MutableLiveData<GameSummary> selectedGame;
    private final MutableLiveData<Integer> errorMessage;

    public GameListModel() {
        db = FirebaseFirestore.getInstance();
        // add  live data
        consoles = new MutableLiveData<>(null);
        games = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        library = new MutableLiveData<>(null);
        wishlist = new MutableLiveData<>(null);
        selectedGame = new MutableLiveData<>(null);
errorMessage = new MutableLiveData<>(null);


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
                    snackbarMessage.postValue(R.string.errorGettingLibrary);
                } else if (querySnapshot != null) {
                    Log.i(LOG_TAG, "Library Found.");

                    List<Library> newLibrary = querySnapshot.toObjects(Library.class);
                   library.postValue(newLibrary);
                }


            });

        wishlistRegistration =
            db.collection("userWishlist").whereEqualTo("userId", userId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    Log.e(LOG_TAG, "Error getting Wishlist.", error);
                    snackbarMessage.postValue(R.string.errorGettingWishlist);
                } else if (querySnapshot != null) {
                    Log.i(LOG_TAG, "Wishlist Found.");

                    List<WishList> newWishlist = querySnapshot.toObjects(WishList.class);
                    wishlist.postValue(newWishlist);
                }


            });

        consolesRegistration =
            db.collection("consoles")
                .orderBy("name")
                .addSnapshotListener((@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {

                    if (error != null) {
                        Log.e(LOG_TAG, "Error getting consoles.", error);
                        snackbarMessage.postValue(R.string.errorGettingConsole);
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

    public LiveData<Integer> getSnackbarMessage() {
        return snackbarMessage;
    }

    public LiveData<List<Consoles>> getConsoles() {
        return consoles;
    }

    public String getFilterConsoleId() {
        return filterConsoleId;
    }

    public LiveData<List<Library>> getLibrary() {
        return library;
    }

    public LiveData<List<WishList>> getWishList() {
        return wishlist;
    }

    public LiveData<GameSummary> getSelectedGame(){return selectedGame;}

    public LiveData<Integer> getErrorMessage(){return errorMessage;}


    public void setSelectedGame(GameSummary game){

        this.selectedGame.postValue(game);
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

        Query query;
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

        }

        if (filterConsoleId != null) {
            query = query.whereEqualTo("consoles." + filterConsoleId, true);
        }

        // FIXME: Sort games by: name, releaseYear
        //        Use orderBy() and add the required indexes in the Firebase Console

        //get game collection
        gamesRegistration =
            query.addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    // show error...
                    Log.e(LOG_TAG, "Error getting games.", error);
                    snackbarMessage.postValue(R.string.errorGettingGame);
                    errorMessage.postValue(R.string.errorGettingGame);

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
                            List<Library> newGamesLibrary = querySnapshot.toObjects(Library.class);
                            for (Library game : newGamesLibrary) {
                                newGameSummary.add(new GameSummary(game));
                            }
                            break;
                        case WISHLIST:
                            List<WishList> newGamesWishlist = querySnapshot.toObjects(WishList.class);
                            for (WishList game : newGamesWishlist) {
                                newGameSummary.add(new GameSummary(game));
                            }
                            break;
                    }
                    games.postValue(newGameSummary);
                    snackbarMessage.postValue(R.string.gameUpdated);
                }

            });


    }

    public void addGameToLibrary(GameSummary game, Map<String, Boolean> selectedConsoles) {

        Library newGame = new Library();
        newGame.userId = userId;
        newGame.gameId = game.id;
        newGame.consoles = game.consoles;
        newGame.description = game.description;
        newGame.releaseYear = game.releaseYear;
        newGame.gameImage = game.gameImage;
        newGame.name = game.name;
        newGame.selectedConsoles = selectedConsoles;

        db.collection("userLibrary")
            .document(userId + ";" + game.id)
            .set(newGame)
            .addOnSuccessListener((result) -> {
                Log.i(LOG_TAG, "Creating document.");
                snackbarMessage.postValue(R.string.libraryAdded);
            })
            .addOnFailureListener((error) -> {
                Log.e(LOG_TAG, "Document not added.", error);
                snackbarMessage.postValue(R.string.libraryAddedError);
            });


    }

    public void removeGameFromLibrary(String gameId) {
        db.collection("userLibrary")
            .document(userId + ";" + gameId)
            .delete()
            .addOnSuccessListener((result) -> {
                Log.i(LOG_TAG, "Document deleted.");
                snackbarMessage.postValue(R.string.libraryRemoved);
            })
            .addOnFailureListener((error) -> {
                Log.e(LOG_TAG, "Document not removed", error);
                snackbarMessage.postValue(R.string.libraryRemovedError);
            });


    }
    public void addGameToWishlist(GameSummary game, Map<String, Boolean> selectedConsoles) {

        WishList newGame = new WishList();
        newGame.userId = userId;
        newGame.gameId = game.id;
        newGame.consoles = game.consoles;
        newGame.description = game.description;
        newGame.releaseYear = game.releaseYear;
        newGame.gameImage = game.gameImage;
        newGame.name = game.name;
        newGame.selectedConsoles = selectedConsoles;

        db.collection("userWishlist")
            .document(userId + ";" + game.id)
            .set(newGame)
            .addOnSuccessListener((result) -> {
                Log.i(LOG_TAG, "Creating document.");
                snackbarMessage.postValue(R.string.wishlistAdded);
            })
            .addOnFailureListener((error) -> {
                Log.e(LOG_TAG, "Document not added.", error);
                snackbarMessage.postValue(R.string.wishlistAddedError);
            });


    }
    public void removeGameFromWishlist(String gameId) {
        db.collection("userWishlist")
            .document(userId + ";" + gameId)
            .delete()
            .addOnSuccessListener((result) -> {
                Log.i(LOG_TAG, "Document deleted.");
                snackbarMessage.postValue(R.string.wishlistRemoved);
            })
            .addOnFailureListener((error) -> {
                Log.e(LOG_TAG, "Document not removed", error);
                snackbarMessage.postValue(R.string.wishlistRemovedError);
            });


    }
}
