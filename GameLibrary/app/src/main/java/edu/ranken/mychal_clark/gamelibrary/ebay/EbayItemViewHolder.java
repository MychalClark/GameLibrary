package edu.ranken.mychal_clark.gamelibrary.ebay;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class EbayItemViewHolder extends RecyclerView.ViewHolder{
    public ImageView ebayItemImage;
    public TextView ebayItemShipping;
    public TextView ebayItemPrice;
    public TextView ebayItemSeller;
    public TextView ebayItemTitle;

    public EbayItemViewHolder(View itemView) {
        super(itemView);
    }
}
