package edu.ranken.mychal_clark.gamelibrary.ui;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MyProfileViewModel extends ViewModel {


    private static final String LOG_TAG = "MyProfileViewModel";

    private final FirebaseFirestore db;


    //

    //live data
    private final MutableLiveData<String> user;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<String> errorMessage;

    private String userId = null;


    public MyProfileViewModel(){
        db = FirebaseFirestore.getInstance();
        //add live data
        user = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);

        // get Current User





    }


    //observe if new user
    public void createUser() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        db.collection("users")
            .whereEqualTo("userId",userId)
            .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if(querySnapshot != null){
                    Log.i(LOG_TAG, "Creating New User ggggggggggggggggggggggg");
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("Email", user.getEmail());
                    newUser.put("userId", userId);

                    db.collection("users").document(userId).set(newUser);
                }

            });}

}
