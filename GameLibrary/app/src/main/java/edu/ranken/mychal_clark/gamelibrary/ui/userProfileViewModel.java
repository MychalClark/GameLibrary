package edu.ranken.mychal_clark.gamelibrary.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import edu.ranken.mychal_clark.gamelibrary.data.User;

public class userProfileViewModel extends ViewModel {

    private static final String LOG_TAG = userProfileViewModel.class.getSimpleName();

    private final FirebaseFirestore db;
    //
    private ListenerRegistration userRegistration;
    private String userId;

    //live data
    private final MutableLiveData<User> user;
    private final MutableLiveData<String> snackbarMessage;



    public userProfileViewModel(){
        db = FirebaseFirestore.getInstance();
        //add live data
        user = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);


}
    @Override
    protected void onCleared() {
        if (userRegistration != null) {
            userRegistration.remove();
        }
        super.onCleared();
    }

    public LiveData<User> getUser(){return user;}

    //clears the snackbar
    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    public void fetchUser(String userId){
        this.userId = userId;

        if (userRegistration != null) {
            userRegistration.remove();
        }

        Log.i(LOG_TAG, "hey "+ userId);

        if(userId == null){
            this.user.postValue(null);
            this.snackbarMessage.postValue("No User selected.");
        }
        else {
           userRegistration =
        db.collection("users")
                .document(userId)
                .addSnapshotListener((document, error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting User.", error);
                        snackbarMessage.postValue("Error getting User.");
                    } else if (document != null && document.exists()) {
                        User user = document.toObject(User.class);
                        this.user.postValue(user);
                        this.snackbarMessage.postValue("Game updated.");
                    } else {
                        this.user.postValue(null);
                        this.snackbarMessage.postValue("Game does not exist.");
                    }
                });
        }
    }
}
