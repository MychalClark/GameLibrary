package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class ReceiptListViewModel extends ViewModel {

    private static final String LOG_TAG = "MyProfileViewModel";

    //firebase
    private final FirebaseFirestore db;
    private FirebaseUser user;

    //listeners
    private ListenerRegistration libraryRegistration;
    private ListenerRegistration wishListRegistration;
    private ListenerRegistration userRegistration;

    //live data
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<Integer> errorMessage;


   public ReceiptListViewModel(){
       db = FirebaseFirestore.getInstance();
       user = FirebaseAuth.getInstance().getCurrentUser();

       snackbarMessage = new MutableLiveData<>(null);
       errorMessage = new MutableLiveData<>(null);

    }
}
