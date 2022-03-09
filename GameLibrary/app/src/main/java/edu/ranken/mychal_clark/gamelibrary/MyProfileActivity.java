package edu.ranken.mychal_clark.gamelibrary;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import edu.ranken.mychal_clark.gamelibrary.ui.MyProfileViewModel;

public class MyProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GameListActivity";

    //States
    private MyProfileViewModel model;
    private Picasso picasso;

    //Create Views
    private TextView emailText;
    private TextView displayNameText;
    private TextView userIdText;
    private ImageView userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //find views
        emailText = findViewById(R.id.profileEmailText);
        userIdText = findViewById(R.id.profileUserIdText);
        displayNameText = findViewById(R.id.profileNameText);
        userImage = findViewById(R.id.profileImage);

        // get picasso
        picasso = Picasso.get();

        //bind model
        model = new ViewModelProvider(this).get(MyProfileViewModel.class);


        emailText.setText(user.getEmail());
        userIdText.setText(user.getUid());
        displayNameText.setText(user.getDisplayName());


        if (user.getUid() == null) {
            userImage.setImageResource(R.drawable.no_image);
        } else {
            userImage.setImageResource(R.drawable.no_image);
            picasso.load(user.getPhotoUrl())
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.no_image)
                .resize(200, 300)
                .centerCrop()
                .into(userImage);
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
}