package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ui.game.GameDetailsFragment;
import edu.ranken.mychal_clark.gamelibrary.ui.game.GameDetailsViewModel;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameDetailsActivity.class.getSimpleName();
    public static final String EXTRA_GAME_ID = "gameId";

    // states
    private String gameId;

    //Create View
    private FragmentContainerView fragmentContainer;
    private GameDetailsFragment fragment;
    private GameDetailsViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragment = new GameDetailsFragment();
        model = new ViewModelProvider(this).get(GameDetailsViewModel.class);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .commit();


        if(savedInstanceState == null) {
            // get intent
            Intent intentTwo = getIntent();
            String intentAction = intentTwo.getAction();
            Uri intentData = intentTwo.getData();

            if (intentAction == null) {
                gameId = intentTwo.getStringExtra(EXTRA_GAME_ID);
            } else if (Objects.equals(intentAction, Intent.ACTION_VIEW) && intentData != null) {
                handleWebLink(intentTwo);
            }
        }else{gameId= savedInstanceState.getString("gameId");
        }

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("gameId", gameId);
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
        String prefix = "/game/";

        // parse uri path
        if (path.startsWith(prefix)) {
            int gameIdEnd = path.indexOf("/", prefix.length());
            if (gameIdEnd < 0) {
                gameId = path.substring(prefix.length());
            } else {
                gameId = path.substring(prefix.length(), gameIdEnd);
            }
        } else {
            gameId = null;
        }

        model.fetchGame(gameId);


}
}