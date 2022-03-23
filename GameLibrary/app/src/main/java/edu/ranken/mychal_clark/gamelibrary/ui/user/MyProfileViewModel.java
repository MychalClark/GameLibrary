package edu.ranken.mychal_clark.gamelibrary.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

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


        // get user pressed


    }
    public LiveData<List<Library>> getLibrary(){return librarys;}

    }
