package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ui.GameDetailsViewModel;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameDetailsActivity.class.getSimpleName();
    public static final String EXTRA_GAME_ID = "gameId";

    // states
    private String gameId;
    private GameDetailsViewModel model;
    private Picasso picasso;

    //Create Views
    private TextView gameTitle;
    private TextView gameDescription;
    private TextView gameTags;
    private ImageView gameMainImage;
    private TextView gameControllerText;
    private TextView gameMultiplayerText;
    private TextView gameGenreText;
    private ImageView[] gameScreenshots;
    private ImageView[] consoleIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        //set views
        gameTitle = findViewById(R.id.gameDetailTitle);
        gameDescription = findViewById(R.id.gameDetailDescription);
        gameTags = findViewById(R.id.gameDetailTags);
        gameMainImage = findViewById(R.id.gameDetailGameImage);
        gameControllerText = findViewById(R.id.gameDetailController);
        gameMultiplayerText = findViewById(R.id.gameDetailMultiplayer);
        gameGenreText = findViewById(R.id.gameDetailGenre);
        gameScreenshots= new ImageView[]{
            findViewById(R.id.gameDetailImage1),
            findViewById(R.id.gameDetailImage2),
            findViewById(R.id.gameDetailImage3)
        };
        consoleIcons = new ImageView[]{
            findViewById(R.id.gameDetailConsole1),
            findViewById(R.id.gameDetailConsole2),
           findViewById(R.id.gameDetailConsole3),
            findViewById(R.id.gameDetailConsole4)
        };

        // get intent
        Intent intent = getIntent();
        gameId = intent.getStringExtra(EXTRA_GAME_ID);

        // get picasso
        picasso = Picasso.get();

        // bind model

        model = new ViewModelProvider(this).get(GameDetailsViewModel.class);
        model.fetchGame(gameId);
        model.getGame().observe(this,(game)->{

            if(game == null){
                Log.i(LOG_TAG, "no game");}
            else{
                //picassoo
                picasso
                    .load(game.gameImage)
                    .noPlaceholder()
                    //.placeholder(R.drawable.ic_downloading)
                    .error(R.drawable.no_image)
                    .resize(200, 300)
                    .centerCrop()
                    .into(gameMainImage);

                //game description stuff.
                gameTitle.setText(game.name);
                gameDescription.setText(game.description);
                gameControllerText.setText("Controller Support: " + game.controllerSupport);
                gameMultiplayerText.setText("Multiplayer Support: " + game.multiplayerSupport);

                if(game.tags != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < game.tags.size(); i++) {
                        sb.append(game.tags.get(i));
                    }
                    gameTags.setText("Tags: " + sb);
                }

                if(game.genre != null){
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < game.genre.size(); i++) {

                        sb.append(game.tags.get(i));
                    }
                    gameGenreText.setText("Genre: " + sb);
                }


                //Icon Setter

                if (game.consoles == null) {
                    for (int i = 0; i < consoleIcons.length; ++i) {
                        consoleIcons[i].setImageResource(0);
                        consoleIcons[i].setVisibility(View.GONE);
                    }
                } else {

                    int iconIndex = 0;

                    for (Map.Entry<String, Boolean> entry : game.consoles.entrySet()) {

                        if (Objects.equals(entry.getValue(), Boolean.TRUE)) {

                            switch (entry.getKey()) {

                                default:
                                    consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                                    consoleIcons[iconIndex].setImageResource(R.drawable.ic_error);
                                    break;
                                case "windows":
                                    consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                                    consoleIcons[iconIndex].setImageResource(R.drawable.ic_windows);
                                    break;
                                case "xbox":
                                    consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                                    consoleIcons[iconIndex].setImageResource(R.drawable.ic_xbox);
                                    break;
                                case "nintendo":
                                    consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                                    consoleIcons[iconIndex].setImageResource(R.drawable.ic_nintendo);
                                    break;
                                case "playstation":
                                    consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                                    consoleIcons[iconIndex].setImageResource(R.drawable.ic_ps);
                                    break;
                            }
                            iconIndex++;
                            if (iconIndex >= consoleIcons.length) {
                                break;
                            }
                        }
                    }
                    for (; iconIndex < consoleIcons.length; ++iconIndex) {
                        consoleIcons[iconIndex].setImageResource(0);
                        consoleIcons[iconIndex].setVisibility(View.GONE);
                    }
                }

                for (int i = 0; i < game.images.size(); i++) {
                    picasso
                        .load(game.images.get(i))
                        .noPlaceholder()
                        //.placeholder(R.drawable.ic_downloading)
                        .error(R.drawable.no_image)
                        .resize(200, 300)
                        .centerInside()
                        .into(gameScreenshots[i]);
                }
            }


        });
    }
}