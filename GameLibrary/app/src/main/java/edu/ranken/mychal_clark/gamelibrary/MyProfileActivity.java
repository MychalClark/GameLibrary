package edu.ranken.mychal_clark.gamelibrary;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import edu.ranken.mychal_clark.gamelibrary.ui.user.MyProfileViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.user.ProfileGameAdapter;

public class MyProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GameListActivity";

    //States
    private MyProfileViewModel model;
    private Picasso picasso;
    private ProfileGameAdapter profileGameAdapter;
    private RecyclerView libraryRecycler;

    //Create Views
    private TextView emailText;
    private TextView displayNameText;
    private TextView userIdText;
    private ImageView userImage;
    private Button cameraBtn;
    private Button galleryBtn;
    



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        libraryRecycler = findViewById(R.id.libraryRecycler);
        //create adapter
        profileGameAdapter = new ProfileGameAdapter(this, null);

        // setup recycler view
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this));
        libraryRecycler.setAdapter(profileGameAdapter);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //find views
        emailText = findViewById(R.id.profileEmailText);
        userIdText = findViewById(R.id.ProfileUserIdText);
        displayNameText = findViewById(R.id.ProfileNameText);
        userImage = findViewById(R.id.profileImage);
        


        // get picasso
        picasso = Picasso.get();

        //bind model
        model = new ViewModelProvider(this).get(MyProfileViewModel.class);
        model.getLibrary().observe(this,(librarys) -> {profileGameAdapter.setItems(librarys);});


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