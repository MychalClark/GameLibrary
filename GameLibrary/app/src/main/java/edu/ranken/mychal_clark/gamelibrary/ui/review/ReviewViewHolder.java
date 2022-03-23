package edu.ranken.mychal_clark.gamelibrary.ui.review;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    public TextView displayName;
    public TextView date;
    public TextView reviewText;


    public ReviewViewHolder(View itemView) {
        super(itemView);
    }
}
