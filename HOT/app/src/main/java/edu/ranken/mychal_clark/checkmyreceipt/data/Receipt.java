package edu.ranken.mychal_clark.checkmyreceipt.data;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Receipt {
    @DocumentId
    public String receiptId;
    public String userId;
    public Double taxPercent;
    public Double taxAmount;
    public Double subtotal;
    public Double total;
    public Double tip10;
    public Double tip20;
    public Double tip30;
    @ServerTimestamp
    public Date updatedOn;


    public Receipt() {
    }

    public Receipt(
        String receiptId, String userId, Double taxPercent,
        Double taxAmount, Double subtotal, Double total,
        Double tip10, Double tip20, Double tip30, Date updatedOn) {

        this.receiptId = receiptId;
        this.userId = userId;
        this.taxPercent = taxPercent;
        this.taxAmount = taxAmount;
        this.subtotal = subtotal;
        this.total = total;
        this.tip10 = tip10;
        this.tip20 = tip20;
        this.tip30 = tip30;
        this.updatedOn = updatedOn;

    }
}
