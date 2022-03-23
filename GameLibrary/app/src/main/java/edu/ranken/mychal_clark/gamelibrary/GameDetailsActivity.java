package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
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

import java.util.Map;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ui.game.GameDetailsViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.review.ReviewListAdapter;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameDetailsActivity.class.getSimpleName();
    public static final String EXTRA_GAME_ID = "gameId";

    // states
    private String gameId;
    private GameDetailsViewModel model;
    private Picasso picasso;
    private RecyclerView recyclerView;
    private ReviewListAdapter reviewsAdapter;

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
    private ImageButton composeReviewButton;

    // FIXME: place after onCreate() method
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_details);

        Log.i(LOG_TAG, "extra" + EXTRA_GAME_ID);
        //set views
        recyclerView = findViewById(R.id.reviewList);
        //create adapter
        reviewsAdapter = new ReviewListAdapter(this, null);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reviewsAdapter);


        gameTitle = findViewById(R.id.gameDetailTitle);
        gameDescription = findViewById(R.id.gameDetailDescription);
        gameTags = findViewById(R.id.gameDetailTags);
        gameMainImage = findViewById(R.id.gameDetailGameImage);
        gameControllerText = findViewById(R.id.gameDetailController);
        gameMultiplayerText = findViewById(R.id.gameDetailMultiplayer);
        gameGenreText = findViewById(R.id.gameDetailGenre);
        gameScreenshots = new ImageView[]{
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

        composeReviewButton = findViewById(R.id.composeReviewButton);

        // get intent
        Intent intent = getIntent();
        gameId = intent.getStringExtra(EXTRA_GAME_ID);


        // get picasso
        picasso = Picasso.get();

        // bind model
        model = new ViewModelProvider(this).get(GameDetailsViewModel.class);

        model.getReviews().observe(this,(reviews) -> {reviewsAdapter.setItems(reviews);});

        model.fetchGame(gameId);
        model.getGame().observe(this, (game) -> {


            if (game == null) {
                Log.i(LOG_TAG, "no game" + gameId);
            } else {

                Log.i(LOG_TAG, "have game" + gameId);

                //picassoo
                if(game.gameImage != null) {
                    gameMainImage.setImageResource(R.drawable.no_image);
                    picasso
                        .load(game.gameImage)
                        .noPlaceholder()
                        //.placeholder(R.drawable.ic_downloading)
                        .error(R.drawable.no_image)
                        .resize(200, 300)
                        .centerCrop()
                        .into(gameMainImage);
                }else{
                    gameMainImage.setImageResource(R.drawable.no_image);
                }

                //game description stuff.

                if (game.name == null) {
                    gameTitle.setText("No Game");
                } else {
                    gameTitle.setText(game.name);
                }

                if (game.description == null) {
                    gameDescription.setText("No Description");
                } else {
                    gameDescription.setText(game.description);
                }

                if (game.controllerSupport == null) {
                    gameControllerText.setText("Controller Support: Unknown ");
                } else {
                    gameControllerText.setText("Controller Support: " + game.controllerSupport);
                }

                if (game.multiplayerSupport == null) {
                    gameMultiplayerText.setText("Multiplayer Support: Unknown");
                } else {
                    gameMultiplayerText.setText("Multiplayer Support: " + game.multiplayerSupport);
                }

                if (game.tags != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < game.tags.size(); i++) {
                        sb.append(game.tags.get(i));
                    }
                    gameTags.setText("Tags: " + sb);
                }

                if (game.genre != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < game.genre.size(); i++) {

                        sb.append(game.tags.get(i));
                    }
                    gameGenreText.setText("Genre: " + sb);
                }
                else{gameGenreText.setText("Genre: Unknown");}



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

                // FIXME: this line is way too long, keep lines under 100 characters
                if(game.images == null || game.images.size() <= 0){gameScreenshots[1].setImageResource(R.drawable.no_image);gameScreenshots[2].setImageResource(R.drawable.no_image);gameScreenshots[3].setImageResource(R.drawable.no_image);}
                else {
                    // FIXME: what if there are not exactly 3 images?
                    //        what if there are 2 or 4 images?
                    for (int i = 0; i < game.images.size(); i++) {
                        gameScreenshots[i].setImageResource(R.drawable.no_image);
                        if (game.images.get(i).isEmpty()) {
                        } else {
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
                }


            }


        });

        // register listeners
        composeReviewButton.setOnClickListener((view) -> {
            Intent intentTwo = new Intent(this, ComposeReviewActivity.class);
            intentTwo.putExtra(ComposeReviewActivity.EXTRA_GAME_ID, gameId);
            startActivity(intentTwo);
        });
    }




}