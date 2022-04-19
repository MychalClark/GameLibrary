package edu.ranken.mychal_clark.gamelibrary.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ComposeReviewActivity;
import edu.ranken.mychal_clark.gamelibrary.EbayBrowseActivity;
import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Game;
import edu.ranken.mychal_clark.gamelibrary.ui.review.ComposeReviewViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.review.ReviewListAdapter;

public class GameDetailsFragment extends Fragment {

    private static final String LOG_TAG = GameDetailsFragment.class.getSimpleName();
    public static final String EXTRA_GAME_ID = "gameId";

    // states
    private String gameId;
    private GameDetailsViewModel model;
    private Picasso picasso;
    private RecyclerView recyclerView;
    private ReviewListAdapter reviewsAdapter;
    private ComposeReviewViewModel reviewViewModel;

    public GameDetailsFragment(){
        super(R.layout.game_details);
    }

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
    private ImageButton shareGameButton;
    private Button ebayBtn;
    private TextView gameAverage;
    private Game selectedGame;

    @Override
    public void onViewCreated(@NonNull View contentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);

        //get Activity
        FragmentActivity activity = getActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();


            //set views
            recyclerView = contentView.findViewById(R.id.reviewList);
            //create adapter
            reviewsAdapter = new ReviewListAdapter(activity, null);

            // setup recycler view
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(reviewsAdapter);

            gameTitle = contentView.findViewById(R.id.gameDetailTitle);
            gameDescription = contentView.findViewById(R.id.gameDetailDescription);
            gameTags = contentView.findViewById(R.id.gameDetailTags);
            gameMainImage = contentView.findViewById(R.id.gameDetailGameImage);
            gameControllerText = contentView.findViewById(R.id.gameDetailController);
            gameMultiplayerText = contentView.findViewById(R.id.gameDetailMultiplayer);
            gameGenreText = contentView.findViewById(R.id.gameDetailGenre);
            gameAverage = contentView.findViewById(R.id.gameDetailAveragePrice);
            gameScreenshots = new ImageView[]{
                contentView.findViewById(R.id.gameDetailImage1),
                contentView.findViewById(R.id.gameDetailImage2),
                contentView.findViewById(R.id.gameDetailImage3)
            };
            consoleIcons = new ImageView[]{
                contentView.findViewById(R.id.gameDetailConsole1),
                contentView.findViewById(R.id.gameDetailConsole2),
                contentView.findViewById(R.id.gameDetailConsole3),
                contentView.findViewById(R.id.gameDetailConsole4)
            };

            composeReviewButton = contentView.findViewById(R.id.composeReviewButton);
            shareGameButton = contentView.findViewById(R.id.shareGameButton);

            // get intent
           Intent intent = activity.getIntent();
            gameId = intent.getStringExtra(EXTRA_GAME_ID);


        Log.i(LOG_TAG, "extra " + gameId);

            // get picasso
            picasso = Picasso.get();

            // bind model
            model = new ViewModelProvider(activity).get(GameDetailsViewModel.class);

            model.getReviews().observe(lifecycleOwner, (reviews) -> {
                reviewsAdapter.setItems(reviews);
            });


            model.getSearchResponse().observe(lifecycleOwner, (searchResponse) -> {

                if (searchResponse != null) {
                    if (searchResponse.itemSummaries != null) {
                        double maxPrice = 0;
                        double maxShipping = 0;

                        for (int i = 0; i < searchResponse.itemSummaries.size(); i++) {
                            maxPrice += Double.parseDouble(searchResponse.itemSummaries.get(i).price.value);

                            if (searchResponse.itemSummaries.get(i).shippingOptions != null &&
                                searchResponse.itemSummaries.get(i).shippingOptions.get(0).shippingCost != null) {
                                maxShipping += Double.parseDouble(searchResponse
                                    .itemSummaries.get(i)
                                    .shippingOptions.get(0)
                                    .shippingCost.value);
                            }
                        }
                        double avg = (maxPrice + maxShipping) / searchResponse.itemSummaries.size();

                        gameAverage.setText("Average Price: " + NumberFormat.getCurrencyInstance().format(avg));
                    } else {
                        gameAverage.setText("Average Price: Unknown");
                    }
                }


            });

            model.getGame().observe(lifecycleOwner, (game) -> {

                selectedGame =  game;

                if (game == null) {
                    if(gameId != null) {
                        model.fetchGame(gameId);
                    }
                } else {
                    gameId = game.id;
                    Log.i(LOG_TAG, "have game" + gameId);

                    //picassoo
                    if (game.gameImage != null) {
                        gameMainImage.setImageResource(R.drawable.no_image);
                        picasso
                            .load(game.gameImage)
                            .noPlaceholder()
                            //.placeholder(R.drawable.ic_downloading)
                            .error(R.drawable.no_image)
                            .resize(200, 300)
                            .centerCrop()
                            .into(gameMainImage);
                    } else {
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
                    } else {
                        gameGenreText.setText("Genre: Unknown");
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

                    if (game.images == null || game.images.size() <= 0) {
                        gameScreenshots[0].setImageResource(R.drawable.no_image);
                        gameScreenshots[1].setImageResource(R.drawable.no_image);
                        gameScreenshots[2].setImageResource(R.drawable.no_image);
                    } else {
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
            composeReviewButton.setOnClickListener((v) -> {
                Intent intentTwo = new Intent(activity, ComposeReviewActivity.class);
                intentTwo.putExtra(ComposeReviewActivity.EXTRA_GAME_ID, gameId);
                startActivity(intentTwo);
            });

            ebayBtn = contentView.findViewById(R.id.ebayListBtn);

            ebayBtn.setOnClickListener((v) -> {
                Intent intentTwo = new Intent(activity, EbayBrowseActivity.class);
                intentTwo.putExtra(ComposeReviewActivity.EXTRA_GAME_ID, gameId);
                startActivity(intentTwo);
            });


            shareGameButton.setOnClickListener((v) -> {
                Log.i(LOG_TAG, "Share game clicked.");

                if (selectedGame == null) {
//                Snackbar.make(view, R.string.errorMovieNotFound, Snackbar.LENGTH_SHORT).show();

                } else if (selectedGame.name == null) {
//                Snackbar.make(view, R.string.movieHasNoName, Snackbar.LENGTH_SHORT).show();

                } else {
                    String gameName;
                    if (selectedGame.releaseYear == null) {
                        gameName = selectedGame.name;
                    } else {
                        gameName = selectedGame.name + " (" + selectedGame.releaseYear + ")";
                    }

                    String message =
                        getString(R.string.shareGameMessage) +
                            gameName +
                            "\nhttps://my-game-list.com/game/" + selectedGame.id;

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);

                    sendIntent.setType("text/plain");

                    startActivity(Intent.createChooser(sendIntent, getString(R.string.shareGame)));
                }
            });


        }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    }

