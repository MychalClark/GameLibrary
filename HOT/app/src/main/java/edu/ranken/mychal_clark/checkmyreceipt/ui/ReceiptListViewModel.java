package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import javax.security.auth.callback.Callback;

import edu.ranken.mychal_clark.checkmyreceipt.R;
import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ReceiptListViewModel extends ViewModel {

    private static final String LOG_TAG = ReceiptListViewModel.class.getSimpleName();

    //firebase
    private final FirebaseFirestore db;
    private FirebaseUser user;

    //listeners
    private ListenerRegistration receiptRegistration;

    //live data
    private final MutableLiveData<Integer> errorMessage;
    private final MutableLiveData<List<Receipt>> receipts;


   public ReceiptListViewModel(){
       db = FirebaseFirestore.getInstance();
       user = FirebaseAuth.getInstance().getCurrentUser();

       errorMessage = new MutableLiveData<>(null);
       receipts = new MutableLiveData<>(null);

      receiptRegistration = db.collection("receipts").whereEqualTo("userId", user.getUid()).addSnapshotListener((@NonNull QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {

           if (error != null) {
               Log.e(LOG_TAG, "Error getting receipts.", error);
               errorMessage.postValue(R.string.errorReceipts);

           } else {
               List<Receipt> newReceipt = querySnapshot != null ? querySnapshot.toObjects(Receipt.class) : null;

               if(querySnapshot == null){
                   errorMessage.postValue(R.string.noReceipts);
               }else {
                   receipts.postValue(newReceipt);
                   errorMessage.postValue(null);
               }

           }
       });

    }

    @Override
    protected void onCleared(){
        if (receiptRegistration !=null){
            receiptRegistration.remove();
        };
        super.onCleared();
    }

    public LiveData<Integer> getErrorMessage(){ return errorMessage;}
    public LiveData<List<Receipt>> getReceipts(){ return receipts;}

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

    public void editReceiptName(String receiptName, Receipt receipt, CreateReceiptCallback callback) {

        db.collection("receipts")
            .document(receipt.receiptId)
            .update("name", receiptName, "updatedOn", new Date())
            .addOnSuccessListener((result)->{
                Log.i(LOG_TAG,"Receipt updated");
                errorMessage.postValue(null);
                callback.onCreateReceipt(receipt.receiptId);
            }).addOnFailureListener((error)->{
            Log.i(LOG_TAG,"Failed to update Receipt");
            errorMessage.postValue(R.string.failReceiptUpdate);

        });

    }
}
