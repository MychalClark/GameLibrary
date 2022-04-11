package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.data.User;
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
    private ImageView xboxIcon;
    private ImageView playstationIcon;
    private ImageView windowsIcon;
    private ImageView nintendoIcon;
    private ImageButton shareUserButton;
    private User selectedUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_scroll);

        //find Views
        userIdText = findViewById(R.id.userProfileUserIdText);
        displayNameText = findViewById(R.id.userProfileNameText);
        userImage = findViewById(R.id.userProfileImage);
        libraryRecycler = findViewById(R.id.userLibraryRecycler);
        wishlistRecycler = findViewById(R.id.userWishlistRecycler);
        xboxIcon = findViewById(R.id.userGameConsole2);
        playstationIcon = findViewById(R.id.userGameConsole3);
        windowsIcon = findViewById(R.id.userGameConsole4);
        nintendoIcon = findViewById(R.id.userGameConsole1);
        shareUserButton = findViewById(R.id.shareUserButton);

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
                selectedUser = user;
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
if(user != null) {
    if (user.preferredConsoles != null) {
        Log.i(LOG_TAG, "got the user brah");


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
        model.getLibrary().observe(this, (librarys) -> {
            profileLibraryGameAdapter.setItems(librarys);
        });
        model.getWishlist().observe(this, (wishlists) -> {
            profileWishlistGameAdapter.setItems(wishlists);
        });

        shareUserButton.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "Share game clicked.");

            if (selectedUser == null) {
//                Snackbar.make(view, R.string.errorMovieNotFound, Snackbar.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "gamey no foundy gggggggggggg");
            } else if (selectedUser.displayName == null) {
//                Snackbar.make(view, R.string.movieHasNoName, Snackbar.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "gamey namey no  foundy LLLLLLLLLLL");
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

        // get intent
        Intent intentTwo = getIntent();
        String intentAction = intentTwo.getAction();
        Uri intentData = intentTwo.getData();

        if (intentAction == null) {
            userId = intentTwo.getStringExtra(EXTRA_USER_ID);
            model.fetchUser(userId);
        } else if (Objects.equals(intentAction, Intent.ACTION_VIEW) && intentData != null) {
            handleWebLink(intentTwo);
        }


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

    private void handleWebLink(Intent intent) {
        Uri uri = intent.getData();
        String path = uri.getPath();
        String prefix = "/user/";

        // parse uri path
        if (path.startsWith(prefix)) {
            int gameIdEnd = path.indexOf("/", prefix.length());
            if (gameIdEnd < 0) {
                userId = path.substring(prefix.length());
            } else {
                userId = path.substring(prefix.length(), gameIdEnd);
            }
        } else {
            userId = null;
        }

}
}