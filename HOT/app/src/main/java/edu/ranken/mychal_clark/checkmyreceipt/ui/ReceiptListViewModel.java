package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import javax.security.auth.callback.Callback;

import edu.ranken.mychal_clark.checkmyreceipt.R;
import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;

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
    private final MutableLiveData<Integer> errorMessage;


   public ReceiptListViewModel(){
       db = FirebaseFirestore.getInstance();
       user = FirebaseAuth.getInstance().getCurrentUser();

       errorMessage = new MutableLiveData<>(null);

    }

    public LiveData<Integer> getErrorMessage(){ return errorMessage;}

    public void createReceipt(String receiptName, CreateReceiptCallback callback) {

        Receipt newReceipt = new Receipt();

        newReceipt.name = receiptName;
        newReceipt.subtotal = 0.00;
        newReceipt.tip10 = 0.00;
        newReceipt.tip20 = 0.00;
        newReceipt.tip30 = 0.00;
        newReceipt.subtotal = 0.00;
        newReceipt.taxAmount = 0.00;
        newReceipt.taxPercent = 0.00;
        newReceipt.total = 0.00;
        newReceipt.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


       db.collection("receipts")
           .add(newReceipt)
           .addOnSuccessListener((result)->{
               Log.i(LOG_TAG,"Receipt added to database");
               errorMessage.postValue(null);
               callback.onCreateReceipt(result.getId());


       }).addOnFailureListener((error)->{
           Log.i(LOG_TAG,"Failed to add Receipt to database");
           errorMessage.postValue(R.string.failAddReceipt);

       });
    }
}
