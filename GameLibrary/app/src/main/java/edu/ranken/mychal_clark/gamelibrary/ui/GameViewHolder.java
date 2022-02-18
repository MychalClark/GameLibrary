package edu.ranken.mychal_clark.gamelibrary.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class GameViewHolder extends RecyclerView.ViewHolder {

    public TextView gameNameText;
    public TextView gameDescriptionText;
    public TextView gameReleaseYearText;
public ImageView gameImage;

    public GameViewHolder(View itemView) {
        super(itemView);
    }
}
