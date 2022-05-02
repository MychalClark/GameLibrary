package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
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

public class TotalsViewModel extends ViewModel {

    //Misc
    private static final String LOG_TAG = TotalsViewModel.class.getSimpleName();

    //Listeners
    private ListenerRegistration receiptRegistration;
    private ListenerRegistration receiptItemRegistration;

    // FIXME: move initialization to constructor
    // FIXME: crashes app when current user is null
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String receiptId;

    //FireBase
    private final FirebaseFirestore db;

    //Live Data Creation
    private final MutableLiveData<Receipt> receipt;
    private final MutableLiveData<List<ReceiptItem>> receiptItems;
    private final MutableLiveData<Integer> errorReceipt;
    private final MutableLiveData<Integer> errorSalesTax;

    public TotalsViewModel() {
        db = FirebaseFirestore.getInstance();
        receipt = new MutableLiveData<>(null);
        receiptItems = new MutableLiveData<>(null);
        errorReceipt = new MutableLiveData<>(null);
        errorSalesTax = new MutableLiveData<>(null);

    }

    @Override
    protected void onCleared() {
        if (receiptItemRegistration != null) {
            receiptItemRegistration.remove();
        }
        ;
        if (receiptRegistration != null) {
            receiptRegistration.remove();
        }
        ;
        super.onCleared();
    }

    //Return Live Data Here
    public LiveData<Receipt> getReceipt() {
        return receipt;
    }

    public LiveData<List<ReceiptItem>> getReceiptItems() {
        return receiptItems;
    }

    public LiveData<Integer> getErrorReceipt() {
        return errorReceipt;
    }

    public LiveData<Integer> getErrorSalesTax() {
        return errorSalesTax;
    }

    //Functions
    public void setSalesTax(double taxPercent) {
        if (receipt != null) {
            if (receipt.getValue().subtotal == null) {
                errorReceipt.postValue(R.string.subTotalNull);
                Log.e(LOG_TAG, "Subtotal is null");
            } else {
                Double subtotal = receipt.getValue().subtotal;
                Double tax = receipt.getValue().subtotal * (taxPercent / 100);  // FIXME: subtotal
                Double total = receipt.getValue().subtotal + tax; // FIXME: subtotal
                Double tip10 = subtotal * .10;
                Double tip20 = subtotal * .20;
                Double tip30 = subtotal * .30;

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

                db.collection("receipts")
                    .document(receiptId).set(newTotal, SetOptions.merge())
                    .addOnSuccessListener((result) -> {
                        errorReceipt.postValue(null);  // FIXME: can't safely clear messages here
                        Log.i(LOG_TAG, "Receipt updated in database...");
                    }).addOnFailureListener((error) -> {
                    errorReceipt.postValue(R.string.receiptNotUpdated);
                    Log.e(LOG_TAG, "Receipt not updated in database.");
                });
            }
        } else {
            errorReceipt.postValue(R.string.noReceiptFound);
            Log.e(LOG_TAG, "No receipt Found");
        }
    }

    // FIXME: method causes confusion, and should be removed
    @Deprecated
    public void clearMessages() {
        errorSalesTax.postValue(null);
        errorReceipt.postValue(null);
    }

    public void fetchReceipt(String receiptId) {

        if (receiptItemRegistration != null) {
            receiptItemRegistration.remove();
        }
        ;
        if (receiptRegistration != null) {
            receiptRegistration.remove();
        }
        ;

        receiptRegistration = db.collection("receipts").document(receiptId).addSnapshotListener((document, error) -> {
            if (error != null) {
                Log.e(LOG_TAG, "Error getting receipt.", error);
                errorReceipt.postValue(R.string.errorReceipt);  // FIXME: conflicts with receiptItems observer
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
                Log.e(LOG_TAG, "Error getting receipts items.", error);
                errorReceipt.postValue(R.string.errorGettingReceiptItems);  // FIXME: conflicts with receipt observer
            } else {
                List<ReceiptItem> newReceiptItems = querySnapshot.toObjects(ReceiptItem.class);
                receiptItems.postValue(newReceiptItems);
                Log.i(LOG_TAG, "receipt items found");
                clearMessages();  // FIXME: can't safely clear messages here

            }
        });


    }

    // FIXME: rename to setSalesTaxError()
    // FIXME: @StringRes
    public void postSaleTaxError(Integer errorMessage) {
        errorSalesTax.postValue(errorMessage);
    }
}
