package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import edu.ranken.mychal_clark.gamelibrary.ui.user.ProfileLibraryGameAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.user.ProfileWishlistGameAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.userProfileViewModel;

public class UserProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();


    public static final String EXTRA_USER_ID = "userId";

    //states
    private String userId;
    private userProfileViewModel model;
    private Picasso picasso;
    private RecyclerView libraryRecycler;
    private RecyclerView wishlistRecycler;
    private ProfileLibraryGameAdapter profileLibraryGameAdapter;
    private ProfileWishlistGameAdapter profileWishlistGameAdapter;

    //create Views
    private TextView displayNameText;
    private TextView userIdText;
    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //find Views
        userIdText = findViewById(R.id.userProfileUserIdText);
        displayNameText = findViewById(R.id.userProfileNameText);
        userImage = findViewById(R.id.userProfileImage);
        libraryRecycler = findViewById(R.id.userLibraryRecycler);
        wishlistRecycler = findViewById(R.id.userWishlistRecycler);

        //create adapter
        profileLibraryGameAdapter = new ProfileLibraryGameAdapter(this, null);
        profileWishlistGameAdapter = new ProfileWishlistGameAdapter(this, null);

        // setup recycler view

        libraryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        libraryRecycler.setAdapter(profileLibraryGameAdapter);
        wishlistRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        wishlistRecycler.setAdapter(profileWishlistGameAdapter);

        // get intent
        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);

        // get picasso
        picasso = Picasso.get();

        //bind model
        model = new ViewModelProvider(this).get(userProfileViewModel.class);
        model.fetchUser(userId);

        model.getUser().observe(this,(user)->{

            if (user == null) {
                displayNameText.setText("No Name");
            } else {
                 displayNameText.setText(user.displayName);
            }
            if (user == null) {
                userIdText.setText("No id");
            } else {
                userIdText.setText(user.id);
            }

            if (user == null) {
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

        });
        model.getLibrary().observe(this, (librarys) -> {
            profileLibraryGameAdapter.setItems(librarys);
        });
        model.getWishlist().observe(this, (wishlists) -> {
            profileWishlistGameAdapter.setItems(wishlists);
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as temporal navigation
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}