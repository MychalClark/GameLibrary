package edu.ranken.mychal_clark.gamelibrary.ui.review;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    public TextView displayName;
    public TextView date;
    public TextView reviewText;
    public Button reviewDeleteBtn;


    public ReviewViewHolder(View itemView) {
        super(itemView);
    }
}
