package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import edu.ranken.mychal_clark.gamelibrary.ui.userProfileViewModel;

public class UserProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();


    public static final String EXTRA_USER_ID = "userId";

    //states
    private String userId;
    private userProfileViewModel model;
    private Picasso picasso;
    private RecyclerView libraryRecycler;

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