package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ranken.mychal_clark.checkmyreceipt.R;
import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ItemListViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "ItemListViewModel";

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
    private final MutableLiveData<Integer> messageReceipt;
    private final MutableLiveData<Integer> messageReceiptItems;


    public ItemListViewModel() {
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        receipt = new MutableLiveData<>(null);
        receiptItems = new MutableLiveData<>(null);
        messageReceipt = new MutableLiveData<>(null);
        messageReceiptItems = new MutableLiveData<>(null);

    }

    //OnCleared Here
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

    //Return Live Data Here
    public LiveData<Receipt> getReceipt() {
        return receipt;
    }

    public LiveData<List<ReceiptItem>> getReceiptItems() {
        return receiptItems;
    }

    public LiveData<Integer> getMessageReceipt() {
        return messageReceipt;
    }

    public LiveData<Integer> getMessageReceiptItems() {
        return messageReceiptItems;
    }


    //Functions
    // FIXME: method causes confusion, and should be removed
    @Deprecated
    public void clearMessages() {
        messageReceipt.postValue(null);
        messageReceiptItems.postValue(null);
    }

    public void deleteItem(String receiptItemId) {
        db.collection("receiptItems")
            .document(receiptItemId)
            .delete()
            .addOnSuccessListener((result)->{
            Log.i(LOG_TAG, "Item Deleted");
            // FIXME: receipt not updated
            // FIXME: can't safely clear messages here
            clearMessages();
        }).addOnFailureListener((error)->{
            Log.i(LOG_TAG, "Item failed to Deleted");  // FIXME: log error
            messageReceiptItems.postValue(R.string.failedToDeleteItems);
        });
    }

    public void deleteAllItems() {

        db.collection("receiptItems")
            .whereEqualTo("receiptId",receiptId)
            .get()
            .addOnSuccessListener(task -> {
            for (DocumentSnapshot document : task.getDocuments()) {
                // FIXME: results not handled
                db.collection("receiptItems").document(document.getId()).delete();
            }
            // FIXME: receipt not updated
            // FIXME: can't safely clear messages here
                clearMessages();
        }).addOnFailureListener((error)->{
            Log.i(LOG_TAG,"No receipt items found");  // FIXME: update message and log error
            messageReceiptItems.postValue(R.string.noReceiptItemsFound); // FIXME: update message
        });
    }

    // FIXME: rename to fetchReceiptItems //fixed//
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
                    messageReceipt.postValue(R.string.errorReceipt);
                } else {
                    Receipt receipt = document.toObject(Receipt.class);
                    this.receipt.postValue(receipt);
                    this.receiptId = receiptId;
                    Log.i(LOG_TAG, "receipt found");
                    clearMessages();  // FIXME: can't safely clear messages here

                }
            });

            receiptItemRegistration = db.collection("receiptItems").whereEqualTo("receiptId", receiptId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                if (error != null) {
                    Log.e(LOG_TAG, "No receipts Found.", error);  // FIXME: update message
                    messageReceiptItems.postValue(R.string.noReceiptFound);  // FIXME: update message
                } else {
                    List<ReceiptItem> newReceiptItems = querySnapshot.toObjects(ReceiptItem.class);
                    receiptItems.postValue(newReceiptItems);
                    clearMessages();  // FIXME: can't safely clear messages here

                }
            });


    }

    public void updateSubtotal(Double subtotal) {
        // FIXME: update other fields
        Map<String, Object> newTotal = new HashMap<>();
        newTotal.put("subtotal", subtotal);

        db.collection("receipts")
            .document(receiptId)
            .set(newTotal, SetOptions.merge())
            .addOnSuccessListener((result)->{
                Log.i(LOG_TAG, "receipt successfully updated.");
            })
            .addOnFailureListener((error)->{
                Log.e(LOG_TAG, "receipt could not be updated.");
                // FIXME: show error message
            });

    }

}
