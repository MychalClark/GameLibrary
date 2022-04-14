package edu.ranken.mychal_clark.gamelibrary.userProfile;

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
import edu.ranken.mychal_clark.gamelibrary.data.Library;
import edu.ranken.mychal_clark.gamelibrary.data.User;
import edu.ranken.mychal_clark.gamelibrary.data.WishList;

public class userProfileViewModel extends ViewModel {

    private static final String LOG_TAG = userProfileViewModel.class.getSimpleName();

    private final FirebaseFirestore db;
    //
    private ListenerRegistration userRegistration;
    private ListenerRegistration libraryRegistration;
    private ListenerRegistration wishListRegistration;
    private String userId;

    //live data
    private final MutableLiveData<User> user;
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<List<Library>> librarys;
    private final MutableLiveData<List<WishList>> wishlists;



    public userProfileViewModel(){
        db = FirebaseFirestore.getInstance();
        //add live data
        user = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        librarys = new MutableLiveData<>(null);
        wishlists = new MutableLiveData<>(null);


}
    @Override
    protected void onCleared() {
        if (userRegistration != null) {
            userRegistration.remove();
        }
        super.onCleared();
    }

    public LiveData<List<Library>> getLibrary(){return librarys;}
    public LiveData<List<WishList>> getWishlist(){return wishlists;}
    public LiveData<User> getUser(){return user;}
    public LiveData<Integer> getSnackbarMessage(){return snackbarMessage;}

    //clears the snackbar
    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    public void fetchUser(String userId){
        this.userId = userId;

        if (userRegistration != null) {
            userRegistration.remove();
        }

        if(userId == null){
            this.user.postValue(null);
            this.snackbarMessage.postValue(R.string.noUserSelected);
        }
        else {
           userRegistration =
        db.collection("users")
                .document(userId)
                .addSnapshotListener((document, error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting User.", error);
                        snackbarMessage.postValue(R.string.errorGettingUsers);
                    } else if (document != null && document.exists()) {
                        User user = document.toObject(User.class);
                        this.user.postValue(user);
                        this.snackbarMessage.postValue(R.string.gameUpdated);
                    } else {
                        this.user.postValue(null);
                        this.snackbarMessage.postValue(R.string.errorGettingGame);
                    }
                });
            libraryRegistration =
                db.collection("userLibrary")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                        if (error != null) {
                            // show error...
                            Log.e(LOG_TAG, "Error getting Library.", error);
                            snackbarMessage.postValue(R.string.errorGettingLibrary);
                        } else {
                            List<Library> newLibrary =
                                querySnapshot != null ? querySnapshot.toObjects(Library.class) : null;
                            librarys.postValue(newLibrary);

                        }
                    });
            wishListRegistration =
                db.collection("userWishlist")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                        if (error != null) {
                            // show error...
                            Log.e(LOG_TAG, "Error getting Wishlist.", error);
                            snackbarMessage.postValue(R.string.errorGettingWishlist);
                        } else {
                            List<WishList> newWishlist =
                                querySnapshot != null ? querySnapshot.toObjects(WishList.class) : null;
                            wishlists.postValue(newWishlist);
                        }
                    });
        }
    }
}
