package edu.ranken.mychal_clark.gamelibrary.ui.userProfile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public TextView displayName;
    public ImageView profilePicture;
    public CardView profileCard;
    public TextView loginDate;


    public UserViewHolder(View itemView) {
        super(itemView);
    }
}
