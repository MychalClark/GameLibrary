package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class AddItemViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "AddItemViewModel";
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String receiptId;
    // FIXME: receiptId //fixed//

    //FireBase
    private final FirebaseFirestore db;

    //Listeners

    //Live Data Creation
    private final MutableLiveData<String> errorMessage; // FIXME: instance variables should be camelCase //fixed//
    private final MutableLiveData<Boolean> receiptSaved;

    public AddItemViewModel(){
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        errorMessage = new MutableLiveData<>(null);
        receiptSaved = new MutableLiveData<>(null);

    }

    //Return Live Data Here
    public LiveData<String> getError(){return errorMessage;}
    public LiveData<Boolean> getReceiptSaved(){return receiptSaved;}

    //Functions
    public void addItem(ReceiptItem item){
        item.userId = userId;
        item.receiptId = receiptId;
        db.collection("receiptItems").add(item).addOnSuccessListener(doc -> {
            receiptSaved.postValue(true);
            Log.i(LOG_TAG, "new item added");
        })
            .addOnFailureListener((OnFailureListener) e -> {
               Log.i(LOG_TAG, "Failed Adding new item");
               // FIXME: show error message
            });

    }

    public void setSellsTaxError(@StringRes Integer messageId){

    }
}
