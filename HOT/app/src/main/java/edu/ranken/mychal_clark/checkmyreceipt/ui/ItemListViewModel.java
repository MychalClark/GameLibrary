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

import java.util.Date;
import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ItemListViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "ItemListViewModel";
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String receiptId;
    // FIXME: receiptId //fixed//

    //FireBase
    private final FirebaseFirestore db;

    //Listeners
    private ListenerRegistration receiptRegistration;
    private ListenerRegistration receiptItemRegistration;


    //Live Data Creation
    private final MutableLiveData<Receipt> receipt;
    private final MutableLiveData<List<ReceiptItem>> receiptItems;
    private final MutableLiveData<String> messageReceipt;       // FIXME: instance variables should be camelCase //fixed//
    private final MutableLiveData<String> messageReceiptItems;  // FIXME: instance variables should be camelCase //fixed//


    public ItemListViewModel() {
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        receipt = new MutableLiveData<>(null);
        receiptItems = new MutableLiveData<>(null);
        messageReceipt = new MutableLiveData<>(null);
        messageReceiptItems = new MutableLiveData<>(null);

    }

    // FIXME: override onCleared so that registrations get removed

    //OnCleared Here
//    @Override
//    protected void OnCleared(){
//        if (receiptItemRegistration !=null){
//            receiptItemRegistration.remove();
//        };
//        if (receiptRegistration !=null){
//            receiptRegistration.remove();
//        };
//        super.onCleared();
//    }

    //Return Live Data Here
    public LiveData<Receipt> getReceipt() {
        return receipt;
    }

    public LiveData<List<ReceiptItem>> getReceiptItems() {
        return receiptItems;
    }

    public LiveData<String> getMessageReceipt() {
        return messageReceipt;
    }

    public LiveData<String> getMessageReceiptItems() {
        return messageReceiptItems;
    }


    //Functions
    public void clearMessages() {
        messageReceipt.postValue(null);
        messageReceiptItems.postValue(null);
    }

    public void deleteItem(String receiptItemId) {
        // FIXME: error handling //fixed//
        db.collection("receiptItems")
            .document(receiptItemId)
            .delete()
            .addOnSuccessListener((result)->{
            Log.i(LOG_TAG, "Item Deleted");
            clearMessages();
        }).addOnFailureListener((error)->{
            Log.i(LOG_TAG, "Item failed to Deleted");
            messageReceiptItems.postValue("items failed to delete");
        });
    }

    public void deleteAllItems() {

        // FIXME: crashes if receipt is null//fixed//
        db.collection("receiptItems")
            .whereEqualTo("receiptId",receiptId)
            .get()
            .addOnSuccessListener(task -> {
            // FIXME: error handling //fixed//
            for (DocumentSnapshot document : task.getDocuments()) {
                db.collection("receiptItems").document(document.getId()).delete();
            }
                clearMessages();
        }).addOnFailureListener((error)->{
            Log.i(LOG_TAG,"No receipt items found");
            messageReceiptItems.postValue("No receipt items found");
        });
    }

    // FIXME: rename to fetchReceiptItems //fixed//
    public void fetchReceipt(String receiptId) {

        receiptRegistration = db.collection("receipts").document(receiptId).addSnapshotListener((document, error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipt.", error);
                messageReceipt.postValue("Error Getting receipt");
            } else {
                Receipt receipt = document.toObject(Receipt.class);
                this.receipt.postValue(receipt);
                this.receiptId = receiptId;
                Log.i(LOG_TAG, "receipt found");
                clearMessages();

            }
        });

        receiptItemRegistration = db.collection("receiptItems").whereEqualTo("receiptId", receiptId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipts items.", error);
                messageReceiptItems.postValue("Error getting receipts items.");
            } else {
                List<ReceiptItem> newReceiptItems = querySnapshot.toObjects(ReceiptItem.class);
                receiptItems.postValue(newReceiptItems);
                clearMessages();

            }
        });


    }

    public void updateSubtotal(Double subtotal) {
        // FIXME: set() and merge, so that receipt can be created
        // FIXME: error handling
        // FIXME: userId vs receiptId
        db.collection("receipts").document(userId).update("subtotal", subtotal);
        //db.collection("receipts").document(userId).update("updatedOn", new Date());

    }

}
