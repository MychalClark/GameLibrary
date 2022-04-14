package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;

public class TotalsViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = "TotalsViewModel";
    private String userId = "Mych";
    // FIXME: receiptId

    //FireBase
    private final FirebaseFirestore db;

    //Listeners
    ListenerRegistration receiptRegistration;

    //Live Data Creation
    private final MutableLiveData<Receipt> receipt;
    private final MutableLiveData<String> errorReceipt;
    private final MutableLiveData<String> errorSalesTax;

    public TotalsViewModel() {
        db = FirebaseFirestore.getInstance();
        receipt = new MutableLiveData<>(null);
        errorReceipt = new MutableLiveData<>(null);
        errorSalesTax = new MutableLiveData<>(null);


        receiptRegistration = db.collection("receipts").document(userId).addSnapshotListener((document, error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipt.", error);
                errorReceipt.postValue("Error Getting receipt");
            } else {
                Receipt receipt = document.toObject(Receipt.class);
                this.receipt.postValue(receipt);
                Log.i(LOG_TAG, "receipt found");

            }
        });
    }

    // FIXME: override onCleared so that registrations get removed

    //Return Live Data Here
    public LiveData<Receipt> getReceipt(){return receipt;}
    public LiveData<String> getErrorReceipt(){return errorReceipt;}
    public LiveData<String> getErrorSalesTax(){return errorSalesTax;}

    // FIXME: inconsistent indentation

//Functions
   public void setSalesTax(double taxPercent){
       // FIXME: crashes if receipt or subtotal is null
       // FIXME: total = subtotal + tax
        Double subtotal = receipt.getValue().subtotal;
       Double total = receipt.getValue().subtotal*(taxPercent/100) + receipt.getValue().subtotal;
        Double tax =  receipt.getValue().subtotal*(taxPercent/100);
        Double tip10 = total *.10;
       Double tip20 = total *.20;
       Double tip30 = total *.30;

       Map<String, Object> newTotal = new HashMap<>();
       newTotal.put("subtotal", subtotal);
       newTotal.put("total", total);
       newTotal.put("taxAmount", tax);
       newTotal.put("tip10", tip10);
       newTotal.put("tip20", tip20);
       newTotal.put("tip30", tip30);
       newTotal.put("userId", userId);
       newTotal.put("updatedOn", new Date()); // FIXME: use server timestamp
       newTotal.put("taxPercent", taxPercent);

       // FIXME: handle errors
       // FIXME: merge
       db.collection("receipts").document(userId).set(newTotal);

       // FIXME: set error message
   }
}
