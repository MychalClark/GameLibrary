package edu.ranken.mychal_clark.gamelibrary.ui.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {


    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    private final FirebaseFirestore db;

    //
    private ListenerRegistration LibraryRegistration;

    //

    //live data
    private final MutableLiveData<String> user;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<String> errorMessage;


    public LoginViewModel() {
        db = FirebaseFirestore.getInstance();
        //add live data
        user = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);


        // get Current User

    }

    //observe if new user
    public void createUser(LoginSuccessCallback callback) {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.i(LOG_TAG, "No user logged in");
            snackbarMessage.postValue("No user logged in.");
        } else {
            String userId = user.getUid();
            Log.i(LOG_TAG, "Updating user document " + userId);

            Map<String, Object> newUser = new HashMap<>();
            newUser.put("Email", user.getEmail());
            newUser.put("userId", userId);
            newUser.put("lastLoggedIn", FieldValue.serverTimestamp());



            db.collection("users")
                .document(userId)
                .set(newUser, SetOptions.merge())
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Log.i(LOG_TAG, "user updated in database.");
                        snackbarMessage.postValue("User updated in database.");
                        callback.onLoginSuccess();
                    } else {
                        Log.e(LOG_TAG, "user not updated in database.", task.getException());
                        snackbarMessage.postValue("Unable to update the database.");
                    }
                });
        }
    }

}
