package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReceiptListViewHolder extends RecyclerView.ViewHolder {

public TextView companyNameLabel;
public TextView totalPriceLabel;
public TextView createdDateLabel;

    public ReceiptListViewHolder(View itemView) {
        super(itemView);
    }
}
