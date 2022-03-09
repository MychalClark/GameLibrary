package edu.ranken.mychal_clark.gamelibrary.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.GameDetailsActivity;
import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.GameChoiceValue;
import edu.ranken.mychal_clark.gamelibrary.data.GameSummary;

public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private static final String LOG_TAG = "GameListAdapter";

    private final AppCompatActivity context;
    private final Picasso picasso;
    private final LayoutInflater layoutInflater;
    private final GameListModel model;

    private List<GameSummary> games;
    private List<GameChoiceValue> choices;


    public GameListAdapter(AppCompatActivity context, GameListModel model) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);

        this.picasso = Picasso.get();
        this.model = model;
    }

//Notify when data changes, add background data to view.

    @SuppressLint("NotifyDataSetChanged")
    public void setGames(List<GameSummary> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setChoice(List<GameChoiceValue> choices) {
        this.choices = choices;
        notifyDataSetChanged();
    }


//    public void setItems(List<Game> items) {
//        this.items = items;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {

        if (games != null) {
            return games.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.game_layout, parent, false);

        GameViewHolder vh = new GameViewHolder(itemView);
        vh.gameNameText = itemView.findViewById(R.id.gameNameText);
        vh.gameDescriptionText = itemView.findViewById(R.id.gameDescriptionText);
        vh.gameImage = itemView.findViewById(R.id.gameImage);
        vh.gameReleaseYearText = itemView.findViewById(R.id.gameReleaseYearText);
        vh.buttonBook = itemView.findViewById(R.id.buttonBook);
        vh.buttonWishlist = itemView.findViewById(R.id.buttonWishlist);

        vh.consoleIcons = new ImageView[]{
            itemView.findViewById(R.id.gameConsole1),
            itemView.findViewById(R.id.gameConsole2),
            itemView.findViewById(R.id.gameConsole3),
            itemView.findViewById(R.id.gameConsole4)
        };

        vh.buttonBook.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "Library pressed");
            GameSummary game = games.get(vh.getAdapterPosition());
        });

        vh.buttonWishlist.setOnClickListener((view) -> {

            GameSummary game = games.get(vh.getAdapterPosition());
            Log.i(LOG_TAG, "Wishlist pressed");


        });

        vh.itemView.setOnClickListener((view) -> {
            GameSummary game = games.get(vh.getAdapterPosition());
            Log.i(LOG_TAG, "clicked on this game:" + game.id);

            // This to change screen to detail page.
            Intent intent = new Intent(context, GameDetailsActivity.class);
            intent.putExtra(GameDetailsActivity.EXTRA_GAME_ID, game.id);
            context.startActivity(intent);

        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder vh, int position) {
        GameSummary game = games.get(position);

        vh.gameNameText.setText(game.name);
        vh.gameDescriptionText.setText(game.description);
        vh.gameReleaseYearText.setText(String.valueOf(game.releaseYear));
        vh.gameImage.setImageResource(R.drawable.no_image);
        vh.buttonBook.setImageResource(R.drawable.bookoutline);
        vh.buttonWishlist.setImageResource(R.drawable.wishlistoutline);


        // If statements

        //Picture Setter
        if (game.gameImage == null || game.gameImage.length() == 0) {
            vh.gameImage.setImageResource(R.drawable.no_image);
        } else {
            vh.gameImage.setImageResource(R.drawable.no_image);
            this.picasso
                .load(game.gameImage)
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.no_image)
                .resize(200, 300)
                .centerCrop()
                .into(vh.gameImage);
        }


        //Icon Setter

        if (game.consoles == null) {
            for (int i = 0; i < vh.consoleIcons.length; ++i) {
                vh.consoleIcons[i].setImageResource(0);
                vh.consoleIcons[i].setVisibility(View.GONE);
            }
        } else {

            int iconIndex = 0;

            for (Map.Entry<String, Boolean> entry : game.consoles.entrySet()) {

                if (Objects.equals(entry.getValue(), Boolean.TRUE)) {

                    switch (entry.getKey()) {

                        default:
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_error);
                            break;
                        case "windows":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_windows);
                            break;
                        case "xbox":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_xbox);
                            break;
                        case "nintendo":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_nintendo);
                            break;
                        case "playstation":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_ps);
                            break;
                    }
                    iconIndex++;
                    if (iconIndex >= vh.consoleIcons.length) {
                        break;
                    }
                }
            }
            for (; iconIndex < vh.consoleIcons.length; ++iconIndex) {
                vh.consoleIcons[iconIndex].setImageResource(0);
                vh.consoleIcons[iconIndex].setVisibility(View.GONE);
            }
        }

//        vh.buttonBook.setVisibility(choices == null ? View.GONE : View.VISIBLE);
//        vh.buttonWishlist.setVisibility(choices == null ? View.GONE : View.VISIBLE);
        vh.buttonBook.setImageResource(R.drawable.bookoutline);
        vh.buttonWishlist.setImageResource(R.drawable.wishlistoutline);
        //votevalue gotta find it

        if (choices != null) {

            for (GameChoiceValue library : choices) {
                if (Objects.equals(game.id, library.libraryUsername)) {

                }

            }
        }
    }


}

