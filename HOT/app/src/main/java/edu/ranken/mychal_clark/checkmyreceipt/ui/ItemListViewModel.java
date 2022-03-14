package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ItemListViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "ItemListViewModel";
    private String userId = "Mych";

    //FireBase
    private final FirebaseFirestore db;

    //Listeners
    private ListenerRegistration receiptRegistration;
    private ListenerRegistration receiptItemRegistration;


    //Live Data Creation
    private final MutableLiveData<Receipt> receipt;
    private final MutableLiveData<List<ReceiptItem>> receiptItems;
    private final MutableLiveData<String> snackbarMessageReceipt;
    private final MutableLiveData<String> snackbarMessageReceiptItems;



    public ItemListViewModel(){
        db = FirebaseFirestore.getInstance();

        //Tie Live Data Here
        receipt = new MutableLiveData<>(null);
        receiptItems = new MutableLiveData<>(null);
        snackbarMessageReceipt = new MutableLiveData<>(null);
        snackbarMessageReceiptItems = new MutableLiveData<>(null);



        //Registrations Here
        receiptRegistration = db.collection("receipts").document(userId).addSnapshotListener((document, error) -> {
            if(error != null){
                Log.e(LOG_TAG, "Error getting receipt.",error);
                snackbarMessageReceipt.postValue("Error Getting receipt");
            }else{
                Receipt receipt = document.toObject(Receipt.class);
                this.receipt.postValue(receipt);
                this.snackbarMessageReceipt.postValue("Receipt Found");
                Log.i(LOG_TAG, "receipt found");

            }
        });

    }

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
    public LiveData<Receipt> getReceipt(){return receipt;}
    public LiveData<List<ReceiptItem>> getReceiptItems(){return receiptItems;}
    public LiveData<String> getMessageReceipt(){return snackbarMessageReceipt;}
    public LiveData<String> getMessageReceiptItems(){return snackbarMessageReceiptItems;}


    //Functions
    public void clearSnackbars() {
        snackbarMessageReceipt.postValue(null);
        snackbarMessageReceiptItems.postValue(null);
    }

    public void deleteItem(String receiptItemId) {
        db.collection("receiptItems").document(receiptItemId).delete();
        Log.i(LOG_TAG, "Item Deleted");
    }

    public void getReceiptId(String receiptId){
        receiptItemRegistration = db.collection("receiptItems").whereEqualTo("receiptId", receiptId).addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
            if(error != null){
                Log.e(LOG_TAG, "Error getting receipts items.",error);
                snackbarMessageReceiptItems.postValue(error.getMessage());
            }else{
                List<ReceiptItem> newReceiptItems = querySnapshot.toObjects(ReceiptItem.class);
                receiptItems.postValue(newReceiptItems);

            }
        });

    }
}
