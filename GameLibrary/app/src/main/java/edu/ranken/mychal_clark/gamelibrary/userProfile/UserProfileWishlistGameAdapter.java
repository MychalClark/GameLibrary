package edu.ranken.mychal_clark.gamelibrary.userProfile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.WishList;
import edu.ranken.mychal_clark.gamelibrary.ui.myProfile.ProfileGameViewHolder;

public class UserProfileWishlistGameAdapter extends RecyclerView.Adapter<ProfileGameViewHolder>{

    private static final String LOG_TAG = "ProfileWishlistAdapter";

    private final Activity context;
    private final LayoutInflater layoutInflater;
    private List<WishList> items;
    private Picasso picasso;


    public UserProfileWishlistGameAdapter(Activity context, List<WishList> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.picasso = picasso.get();
    }

        public void setItems(List<WishList> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public ProfileGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.profile_game_item, parent, false);

        ProfileGameViewHolder vh = new ProfileGameViewHolder(itemView);
        vh.profileGameImage = itemView.findViewById(R.id.profileGameImage);
        vh.profileGameText = itemView.findViewById(R.id.profileGameText);


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileGameViewHolder vh, int position) {

        WishList item = items.get(position);

        vh.profileGameText.setText(item.name);




        // Image Setter
        if (item.gameImage == null || item.gameImage.length() == 0) {
            vh.profileGameImage.setImageResource(R.drawable.no_image);
        }
        else {
            vh.profileGameImage.setImageResource(R.drawable.no_image);
            this.picasso
                .load(item.gameImage)
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.no_image)
                .resize(200, 300)
                .centerCrop()
                .into(vh.profileGameImage);
        }


    }
}
