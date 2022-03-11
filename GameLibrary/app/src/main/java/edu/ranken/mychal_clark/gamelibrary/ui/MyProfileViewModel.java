package edu.ranken.mychal_clark.gamelibrary.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ranken.mychal_clark.gamelibrary.data.Library;

public class MyProfileViewModel extends ViewModel {


    private static final String LOG_TAG = "MyProfileViewModel";

    private final FirebaseFirestore db;

    //
    private ListenerRegistration LibraryRegistration;

    //

    //live data
    private final MutableLiveData<String> user;
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Library>> librarys;

    private String userId = null;


    public MyProfileViewModel(){
        db = FirebaseFirestore.getInstance();
        //add live data
        user = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        librarys = new MutableLiveData<>(null);


        // get Current User

    }
    public LiveData<List<Library>> getLibrary(){return librarys;}



    //observe if new user
    public void createUser() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        LibraryRegistration =
            db.collection("userLibrary")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting Library.", error);
                        snackbarMessage.postValue("Error getting Library.");
                    } else {
                        List<Library> newLibrarys =
                            querySnapshot != null ? querySnapshot.toObjects(Library.class) : null;

                        librarys.postValue(newLibrarys);

                        Log.i(LOG_TAG, "Got the games.");
                        snackbarMessage.postValue("Library Updated.");
                        // show games...

                    }
                });

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
