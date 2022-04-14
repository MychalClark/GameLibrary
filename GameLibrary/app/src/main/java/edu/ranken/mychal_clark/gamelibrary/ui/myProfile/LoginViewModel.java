package edu.ranken.mychal_clark.gamelibrary.ui.myProfile;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import edu.ranken.mychal_clark.gamelibrary.R;

public class LoginViewModel extends ViewModel {

    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    private final FirebaseFirestore db;

    //live data
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<String> errorMessage;


    public LoginViewModel() {
        db = FirebaseFirestore.getInstance();

        //add live data

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
            snackbarMessage.postValue(R.string.noUserLogged);
        } else {
            String userId = user.getUid();
            Log.i(LOG_TAG, "Updating user document " + userId);

            Map<String, Object> newUser = new HashMap<>();
            newUser.put("displayName", user.getDisplayName());
            if(user.getPhotoUrl() == null) {
                newUser.put("profilePictureUrl",null);
            }else{
                newUser.put("profilePictureUrl", user.getPhotoUrl().toString());
            }

            newUser.put("lastLoggedIn", FieldValue.serverTimestamp());

            db.collection("users")
                .document(userId)
                .set(newUser, SetOptions.merge())
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Log.i(LOG_TAG, "user updated in database.");
                        snackbarMessage.postValue(R.string.userUpdated);
                        callback.onLoginSuccess();
                    } else {
                        Log.e(LOG_TAG, "user not updated in database.", task.getException());
                        snackbarMessage.postValue(R.string.updateDatabaseError);
                    }
                });
        }
    }

}
