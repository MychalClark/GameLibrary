package edu.ranken.mychal_clark.checkmyreceipt.data;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ReceiptItem {

    @DocumentId
    public String receiptItemId;
    public String receiptId;
    public String userId;
    public Double price;
    public Integer quantity;
    public Double discountPercent;
    @ServerTimestamp
    public Date addedOn;

    public ReceiptItem() {
    }

    public ReceiptItem
        (String receiptItemId, String receiptId, String userId,
         Double price, Integer quantity, Double discountPercent, Date addedOn) {

        this.receiptId = receiptId;
        this. receiptItemId = receiptItemId;
        this.userId = userId;
        this.price = price;
        this.quantity = quantity;
        this.discountPercent = discountPercent;
        this.addedOn = addedOn;

    }
}
