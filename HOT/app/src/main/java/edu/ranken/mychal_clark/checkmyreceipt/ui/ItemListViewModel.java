package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private String userId = "Mych";
    // FIXME: receiptId

    //FireBase
    private final FirebaseFirestore db;

    //Listeners
    private ListenerRegistration receiptRegistration;
    private ListenerRegistration receiptItemRegistration;


    //Live Data Creation
    private final MutableLiveData<Receipt> receipt;
    private final MutableLiveData<List<ReceiptItem>> receiptItems;
    private final MutableLiveData<String> MessageReceipt;       // FIXME: instance variables should be camelCase
    private final MutableLiveData<String> MessageReceiptItems;  // FIXME: instance variables should be camelCase


    public ItemListViewModel() {
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        receipt = new MutableLiveData<>(null);
        receiptItems = new MutableLiveData<>(null);
        MessageReceipt = new MutableLiveData<>(null);
        MessageReceiptItems = new MutableLiveData<>(null);


        //Registrations Here
        receiptRegistration = db.collection("receipts").document(userId).addSnapshotListener((document, error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipt.", error);
                MessageReceipt.postValue("Error Getting receipt");
            } else {
                Receipt receipt = document.toObject(Receipt.class);
                this.receipt.postValue(receipt);
                this.MessageReceipt.postValue("Receipt Found");
                Log.i(LOG_TAG, "receipt found");

            }
        });

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
        return MessageReceipt;
    }

    public LiveData<String> getMessageReceiptItems() {
        return MessageReceiptItems;
    }


    //Functions
    public void clearMessages() {
        MessageReceipt.postValue(null);
        MessageReceiptItems.postValue(null);
    }

    public void deleteItem(String receiptItemId) {
        // FIXME: error handling
        db.collection("receiptItems").document(receiptItemId).delete();
        Log.i(LOG_TAG, "Item Deleted");
    }

    public void deleteAllItems() {
        Receipt receipt = this.getReceipt().getValue();

        // FIXME: crashes if receipt is null
        db.collection("receiptItems").whereEqualTo("receiptId", receipt.receiptId).get().addOnCompleteListener(task -> {
            // FIXME: error handling
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                db.collection("receiptItems").document(document.getId()).delete();

            }
        });
    }

    // FIXME: rename to fetchReceiptItems
    public void getReceiptId(String receiptId) {
        receiptItemRegistration = db.collection("receiptItems").whereEqualTo("receiptId", receiptId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipts items.", error);
                MessageReceiptItems.postValue("Error getting receipts items.");
            } else {
                List<ReceiptItem> newReceiptItems = querySnapshot.toObjects(ReceiptItem.class);
                receiptItems.postValue(newReceiptItems);

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
