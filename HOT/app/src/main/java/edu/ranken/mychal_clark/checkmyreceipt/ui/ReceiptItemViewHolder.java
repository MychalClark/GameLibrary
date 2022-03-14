package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReceiptItemViewHolder extends RecyclerView.ViewHolder {

    public TextView itemTotalPriceText;
    public TextView itemQuantityText;
    public TextView itemDiscountText;
    public TextView itemPriceText;
    public TextView itemDeleteBtn;

    public ReceiptItemViewHolder(View itemView) {
        super(itemView);
    }
}
