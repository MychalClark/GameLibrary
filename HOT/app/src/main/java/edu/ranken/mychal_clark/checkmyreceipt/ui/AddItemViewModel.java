package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class AddItemViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "AddItemViewModel";
    private String userId = "Mych";

    //FireBase
    private final FirebaseFirestore db;

    //Listeners

    //Live Data Creation
    private final MutableLiveData<String> ErrorMessage;
    private final MutableLiveData<Boolean> receiptSaved;

    public AddItemViewModel(){
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        ErrorMessage = new MutableLiveData<>(null);
        receiptSaved = new MutableLiveData<>(null);

    }

    //Return Live Data Here
    public LiveData<String> getError(){return ErrorMessage;}
    public LiveData<Boolean> getReceiptSaved(){return receiptSaved;}

    //Functions
    public void addItem(ReceiptItem item){
        item.userId = userId;
        item.receiptId = userId;
        db.collection("receiptItems").add(item).addOnSuccessListener(doc -> {
            receiptSaved.postValue(true);
            Log.i(LOG_TAG, "new item added");
        })
            .addOnFailureListener((OnFailureListener) e -> {
               Log.i(LOG_TAG, "Failed Adding new item");
            });

    }
}
