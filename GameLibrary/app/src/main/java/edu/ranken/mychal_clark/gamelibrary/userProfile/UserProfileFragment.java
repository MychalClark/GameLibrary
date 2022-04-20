package edu.ranken.mychal_clark.gamelibrary.userProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.UserProfileActivity;
import edu.ranken.mychal_clark.gamelibrary.data.User;

public class UserProfileFragment extends Fragment {

    private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();


    public static final String EXTRA_USER_ID = "userId";

    //states
    private String userId;
    private UserProfileViewModel model;
    private Picasso picasso;
    private RecyclerView libraryRecycler;
    private RecyclerView wishlistRecycler;
    private UserProfileLibraryGameAdapter profileLibraryGameAdapter;
    private UserProfileWishlistGameAdapter profileWishlistGameAdapter;

    //create Views
    private TextView displayNameText;
    private TextView userIdText;
    private ImageView userImage;
    private ImageView xboxIcon;
    private ImageView playstationIcon;
    private ImageView windowsIcon;
    private ImageView nintendoIcon;
    private ImageButton shareUserButton;
    private User selectedUser;
    private TextView displayNameLabel;
    private TextView userIdLabel;
    private TextView errorMessage;

    public UserProfileFragment() {
        super(R.layout.user_profile_scroll);
    }

    @Override
    public void onViewCreated(@NonNull View contentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);

        //get Activity
        FragmentActivity activity = getActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        //find Views
        userIdText = contentView.findViewById(R.id.userProfileUserIdText);
        displayNameText = contentView.findViewById(R.id.userProfileNameText);
        userImage = contentView.findViewById(R.id.userProfileImage);
        libraryRecycler = contentView.findViewById(R.id.userLibraryRecycler);
        wishlistRecycler = contentView.findViewById(R.id.userWishlistRecycler);
        xboxIcon = contentView.findViewById(R.id.userGameConsole2);
        playstationIcon = contentView.findViewById(R.id.userGameConsole3);
        windowsIcon = contentView.findViewById(R.id.userGameConsole4);
        nintendoIcon = contentView.findViewById(R.id.userGameConsole1);
        shareUserButton = contentView.findViewById(R.id.shareUserButton);
        userIdLabel = contentView.findViewById(R.id.userProfileIdLabel);
        displayNameLabel = contentView.findViewById(R.id.userProfileDisplayNameLabel);
        errorMessage = contentView.findViewById(R.id.userProfileErrorMessage);

        //create adapter
        profileLibraryGameAdapter = new UserProfileLibraryGameAdapter(activity, null);
        profileWishlistGameAdapter = new UserProfileWishlistGameAdapter(activity, null);

        // setup recycler view

        libraryRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        libraryRecycler.setAdapter(profileLibraryGameAdapter);
        wishlistRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        wishlistRecycler.setAdapter(profileWishlistGameAdapter);

        // get intent
//        Intent intent = activity.getIntent();
//        userId = intent.getStringExtra(EXTRA_USER_ID);

        // get picasso
        picasso = Picasso.get();

        //bind model
        model = new ViewModelProvider(activity).get(UserProfileViewModel.class);
        //
        if(userId != null){
            model.fetchUser(userId);
        }

        model.getUser().observe(lifecycleOwner, (user) -> {


            if (user == null) {
                displayNameText.setVisibility(View.GONE);
                userIdText.setVisibility(View.GONE);
                userImage.setVisibility(View.GONE);
                userIdLabel.setVisibility(View.GONE);
                displayNameLabel.setVisibility(View.GONE);
            } else {
                selectedUser = user;
                displayNameText.setVisibility(View.VISIBLE);
                userIdText.setVisibility(View.VISIBLE);
                userImage.setVisibility(View.VISIBLE);
                userIdLabel.setVisibility(View.VISIBLE);
                displayNameLabel.setVisibility(View.VISIBLE);


                if(user.displayName == null){
                    displayNameText.setText(R.string.noDisplayName);
                }else{
                    displayNameText.setText(user.displayName);
                }
                if(user.id == null){
                    userIdText.setText(R.string.noUserId);
                }else{
                    userIdText.setText(user.id);
                }

                if (user.profilePictureUrl == null) {
                    userImage.setImageResource(R.drawable.no_image);
                } else {
                    userImage.setImageResource(R.drawable.no_image);
                    picasso.load(user.profilePictureUrl)
                        .noPlaceholder()
                        //.placeholder(R.drawable.ic_downloading)
                        .error(R.drawable.no_image)
                        .resize(200, 300)
                        .centerCrop()
                        .into(userImage);
                }

                    if (user.preferredConsoles != null) {

                        if (user.preferredConsoles.containsKey("xbox")) {
                            xboxIcon.setVisibility(View.VISIBLE);
                        } else {
                            xboxIcon.setVisibility(View.INVISIBLE);
                        }
                        if (user.preferredConsoles.containsKey("playstation")) {

                            playstationIcon.setVisibility(View.VISIBLE);
                        } else {
                            playstationIcon.setVisibility(View.INVISIBLE);
                        }
                        if (user.preferredConsoles.containsKey("nintendo")) {

                            nintendoIcon.setVisibility(View.VISIBLE);
                        } else {
                            nintendoIcon.setVisibility(View.INVISIBLE);
                        }
                        if (user.preferredConsoles.containsKey("windows")) {

                            windowsIcon.setVisibility(View.VISIBLE);
                        } else {
                            windowsIcon.setVisibility(View.INVISIBLE);
                        }

                    }
                }

        });
        model.getLibrary().observe(lifecycleOwner, (librarys) -> {
            profileLibraryGameAdapter.setItems(librarys);
        });
        model.getWishlist().observe(lifecycleOwner, (wishlists) -> {
            profileWishlistGameAdapter.setItems(wishlists);
        });

        model.getErrorMessage().observe(lifecycleOwner, (messageId) ->{

            if (messageId != null) {
                errorMessage.setText(messageId);
                errorMessage.setVisibility(View.VISIBLE);
            } else {
                errorMessage.setText(null);
                errorMessage.setVisibility(View.GONE);
            }
        });
        model.getSnackbarMessage().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                Snackbar.make(displayNameText, messageId, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            } else {
                // no message to show
            }
        });

        shareUserButton.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "Share game clicked.");

            if (selectedUser == null) {
                Snackbar.make(view, R.string.noUserSelected, Snackbar.LENGTH_SHORT).show();

            } else if (selectedUser.displayName == null) {
                Snackbar.make(view, R.string.userFound, Snackbar.LENGTH_SHORT).show();
            } else {
                String gameName;
                if (selectedUser.displayName == null) {
                    gameName = selectedUser.id;
                } else {
                    gameName = selectedUser.id + " (" + selectedUser.displayName + ")";
                }

                String message =
                    getString(R.string.shareUserMessage) +
                        gameName +
                        "\nhttps://my-user-list.com/user/" + selectedUser.id;

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);

                sendIntent.setType("text/plain");

                startActivity(Intent.createChooser(sendIntent, getString(R.string.shareUser)));
            }
        });


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userId", userId);
    }
}
