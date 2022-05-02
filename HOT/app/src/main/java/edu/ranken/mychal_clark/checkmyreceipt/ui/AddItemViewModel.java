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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.R;
import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class AddItemViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "AddItemViewModel";

    // FIXME: move initialization to constructor
    // FIXME: crashes app when current user is null
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String receiptId;


    //FireBase
    private final FirebaseFirestore db;

    //Listeners
    private ListenerRegistration receiptRegistration;
    private ListenerRegistration receiptItemRegistration;

    //Live Data Creation
    private final MutableLiveData<Receipt> receipt;
    private final MutableLiveData<List<ReceiptItem>> receiptItems;
    private final MutableLiveData<Integer> errorMessage;
    private final MutableLiveData<Boolean> receiptSaved;

    public AddItemViewModel(){
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        receipt = new MutableLiveData<>(null);
        receiptItems = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        receiptSaved = new MutableLiveData<>(null);  // FIXME: initialize as false

    }

    @Override
    protected void onCleared(){
        if (receiptItemRegistration !=null){
            receiptItemRegistration.remove();
        };
        if (receiptRegistration !=null){
            receiptRegistration.remove();
        };
        super.onCleared();
    }

    public void fetchReceipt(String receiptId) {

        if (receiptItemRegistration !=null){
            receiptItemRegistration.remove();
        };
        if (receiptRegistration !=null){
            receiptRegistration.remove();
        };

        receiptRegistration = db.collection("receipts").document(receiptId).addSnapshotListener((document, error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipt.", error);
                errorMessage.postValue((R.string.errorReceipt));
            } else {
                Receipt receipt = document.toObject(Receipt.class);
                this.receipt.postValue(receipt);
                this.receiptId = receiptId;
                Log.i(LOG_TAG, "receipt found");
                errorMessage.postValue(null);  // FIXME: can't safely clear messages here

            }
        });

        receiptItemRegistration = db.collection("receiptItems").whereEqualTo("receiptId", receiptId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "No receipts Found.", error);
               errorMessage.postValue(R.string.noReceiptsFound);
            } else {
                List<ReceiptItem> newReceiptItems = querySnapshot.toObjects(ReceiptItem.class);
                receiptItems.postValue(newReceiptItems);
                errorMessage.postValue(null);  // FIXME: can't safely clear messages here

            }
        });


    }

    //Return Live Data Here
    public LiveData<Integer> getErrorMessage(){return errorMessage;}
    public LiveData<Boolean> getReceiptSaved(){return receiptSaved;}

    //Functions
    public void addItem(ReceiptItem item){
        item.userId = userId;
        item.receiptId = receiptId;
        item.itemTotal = (item.price * item.quantity) * (1 - item.discountPercent / 100);

        db.collection("receiptItems").add(item).addOnSuccessListener(doc -> {
            receiptSaved.postValue(true);
            Log.i(LOG_TAG, "new item added");
            // FIXME: receipt not updated
            // FIXME: can't safely clear messages here
            errorMessage.postValue(null);
        })
            .addOnFailureListener((OnFailureListener) e -> {
               Log.i(LOG_TAG, "Failed Adding new item");  // FIXME: log error
               errorMessage.postValue(R.string.failedNewItem);
            });

    }

    // FIXME: rename to setErrorMessage
    public void setSellsTaxError(@StringRes Integer messageId){

       errorMessage.postValue(messageId);
    }
}
