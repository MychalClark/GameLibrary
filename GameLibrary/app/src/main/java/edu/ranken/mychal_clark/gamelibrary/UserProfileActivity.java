package edu.ranken.mychal_clark.gamelibrary;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.userProfile.UserProfileFragment;
import edu.ranken.mychal_clark.gamelibrary.userProfile.UserProfileViewModel;

public class UserProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();
    public static final String EXTRA_USER_ID = "userId";




    //states
    private String userId;

    //create view
    private FragmentContainerView fragmentContainer;
    private UserProfileViewModel model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        model = new ViewModelProvider(this).get(UserProfileViewModel.class);


        if(savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, UserProfileFragment.class, null)
                .commit();
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
        }else{userId= savedInstanceState.getString("userId");
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("userId", userId);
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

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, LoginActivity.class));
        stackBuilder.addNextIntent(new Intent(this, HomeActivity.class));
        stackBuilder.addNextIntent(new Intent(this, UserProfileActivity.class).putExtra(EXTRA_USER_ID, userId));
        stackBuilder.startActivities();

        model.fetchUser(userId);
}
}