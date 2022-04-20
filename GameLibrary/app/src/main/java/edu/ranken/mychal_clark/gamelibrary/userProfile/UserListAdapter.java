package edu.ranken.mychal_clark.gamelibrary.userProfile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.User;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private static final String LOG_TAG = "UserListAdapter";

    private final FragmentActivity context;
    private final Picasso picasso;
    private final LayoutInflater layoutInflater;
    private final UserListViewModel model;

    private List<User> items;

    public UserListAdapter(FragmentActivity context, UserListViewModel model){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.picasso = Picasso.get();
        this.model = model;
        this.items = items;
    }
    public void setItems(List<User> items) {
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
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = layoutInflater.inflate(R.layout.user_list_item, parent, false);

        UserViewHolder vh = new UserViewHolder(itemView);
        vh.displayName = itemView.findViewById(R.id.userListNameText);
        vh.profileCard = itemView.findViewById(R.id.profileCardList);
        vh.profilePicture = itemView.findViewById(R.id.userListProfilePicture);

        vh.itemView.setOnClickListener((view)->{

            User user = items.get(vh.getAdapterPosition());
            Log.i(LOG_TAG, "clicked on this user:" + user.id);

            // This to change screen to detail page.
            model.setSelectedUser(user);

//            Intent intent = new Intent(context, UserProfileActivity.class);
//            intent.putExtra(UserProfileActivity.EXTRA_USER_ID, user.id);
//            context.startActivity(intent);

        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder vh, int position) {
        User user = items.get(position);

        vh.displayName.setText(user.displayName);

        if (user.profilePictureUrl == null || user.profilePictureUrl.length() == 0) {
            vh.profilePicture.setImageResource(R.drawable.no_image);
        } else {
            vh.profilePicture.setImageResource(R.drawable.no_image);
            this.picasso
                .load(user.profilePictureUrl)
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.no_image)
                .resize(200, 300)
                .centerCrop()
                .into(vh.profilePicture);
        }

    }
}
