package edu.ranken.mychal_clark.gamelibrary.ui.game;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class GameViewHolder extends RecyclerView.ViewHolder {

    public TextView gameNameText;
    public TextView gameDescriptionText;
    public TextView gameReleaseYearText;
    public ImageView gameImage;
    public ImageButton buttonBook;
    public ImageButton buttonWishlist;
    public ImageView[] consoleIcons;
    public boolean inLibrary;
    public boolean inWishlist;

    public GameViewHolder(View itemView) {
        super(itemView);
    }
}
