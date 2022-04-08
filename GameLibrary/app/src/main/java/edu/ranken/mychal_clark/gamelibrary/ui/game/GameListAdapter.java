package edu.ranken.mychal_clark.gamelibrary.ui.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ConsoleChooserDialog;
import edu.ranken.mychal_clark.gamelibrary.GameDetailsActivity;
import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.GameList;
import edu.ranken.mychal_clark.gamelibrary.data.GameSummary;
import edu.ranken.mychal_clark.gamelibrary.data.Library;
import edu.ranken.mychal_clark.gamelibrary.data.WishList;

public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private static final String LOG_TAG = "GameListAdapter";

    private final FragmentActivity context;
    private final Picasso picasso;
    private final LayoutInflater layoutInflater;
    private final GameListModel model;

    private List<GameSummary> games;
    private List<Library> library;
    private List<WishList> wishlist;
    private GameList mode;

    public GameListAdapter(FragmentActivity context, GameListModel model) {
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
    public void setLibrary(List<Library> library) {
        this.library = library;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWishlist(List<WishList> wishlist) {
        this.wishlist = wishlist;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMode(GameList mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

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
        View itemView = layoutInflater.inflate(R.layout.game_item, parent, false);

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
            if(vh.inLibrary){
                model.removeGameFromLibrary(game.id);
                vh.inLibrary = false;
                vh.buttonBook.setImageResource(R.drawable.bookoutline);
            }else{
                ConsoleChooserDialog consoleChooserDialog = new ConsoleChooserDialog(
                    context,
                    "Choose Consoles",
                    game.consoles,
                    null,
                    (selected) ->{
                        if(!selected.isEmpty()) {
                            Map<String, Boolean> selectedConsoles = new HashMap<>();
                            if (selected.containsKey("xbox")) {
                                selectedConsoles.put("xbox",Boolean.TRUE);
                            }
                            if (selected.containsKey("windows")) {
                                selectedConsoles.put("windows",Boolean.TRUE);
                            }
                            if (selected.containsKey("playstation")) {
                                selectedConsoles.put("playstation",Boolean.TRUE);
                            }
                            if (selected.containsKey("nintendo")) {
                                selectedConsoles.put("nintendo",Boolean.TRUE);
                            }

                            model.addGameToLibrary(game, selectedConsoles);
                            vh.inLibrary = true;
                            vh.buttonBook.setImageResource(R.drawable.book);
                        }else{
                            Toast.makeText(view.getContext(), "No Console selected! Item not added.",
                                Toast.LENGTH_LONG).show();
                        }

                    });
                consoleChooserDialog.show();

                }

        });

        vh.buttonWishlist.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "Wishlist pressed");
            GameSummary game = games.get(vh.getAdapterPosition());
            if(vh.inWishlist){
                model.removeGameFromWishlist(game.id);
                vh.inWishlist = false;
                vh.buttonWishlist.setImageResource(R.drawable.wishlistoutline);
            }else{
               ConsoleChooserDialog consoleChooserDialog = new ConsoleChooserDialog(
                   context,
                   "Choose Consoles",
                   game.consoles,
                   null,
                   (selected) ->{
                       if(!selected.isEmpty()) {
                           Map<String, Boolean> selectedConsoles = new HashMap<>();
                           if (selected.containsKey("xbox")) {
                               selectedConsoles.put("xbox",Boolean.TRUE);
                           }
                           if (selected.containsKey("windows")) {
                               selectedConsoles.put("windows",Boolean.TRUE);
                           }
                           if (selected.containsKey("playstation")) {
                               selectedConsoles.put("playstation",Boolean.TRUE);
                           }
                           if (selected.containsKey("nintendo")) {
                               selectedConsoles.put("nintendo",Boolean.TRUE);
                           }

                           model.addGameToWishlist(game, selectedConsoles);
                           vh.inWishlist = true;
                           vh.buttonWishlist.setImageResource(R.drawable.wishlist);
                       }else{
                           Toast.makeText(view.getContext(), "No Console selected! Item not added.",
                               Toast.LENGTH_LONG).show();
                       }

                   });
               consoleChooserDialog.show();

                }



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

            Map<String,Boolean> gameConsoles = game.consoles;
            if (game.selectedConsoles != null) {
                gameConsoles = game.selectedConsoles;
            }

            for (Map.Entry<String, Boolean> entry : gameConsoles.entrySet()) {

                if (Objects.equals(entry.getValue(), Boolean.TRUE)) {

                    switch (entry.getKey()) {

                        default:
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_error);
                            vh.consoleIcons[iconIndex].setContentDescription(context.getString(R.string.console_unknown));
                            break;
                        case "windows":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_windows);
                            vh.consoleIcons[iconIndex].setContentDescription(context.getString(R.string.console_windows));
                            break;
                        case "xbox":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_xbox);
                            vh.consoleIcons[iconIndex].setContentDescription(context.getString(R.string.console_xbox));

                            break;
                        case "nintendo":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_nintendo);
                            vh.consoleIcons[iconIndex].setContentDescription(context.getString(R.string.console_nintendo));
                            break;
                        case "playstation":
                            vh.consoleIcons[iconIndex].setVisibility(View.VISIBLE);
                            vh.consoleIcons[iconIndex].setImageResource(R.drawable.ic_ps);
                            vh.consoleIcons[iconIndex].setContentDescription(context.getString(R.string.console_playstation));
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


        vh.buttonBook.setVisibility(library == null ? View.GONE : View.VISIBLE);
        vh.buttonWishlist.setVisibility(wishlist == null ? View.GONE : View.VISIBLE);
        vh.buttonBook.setImageResource(R.drawable.bookoutline);
        vh.buttonWishlist.setImageResource(R.drawable.wishlistoutline);
        vh.inLibrary = false;
        vh.inWishlist = false;


        if(library != null) {
            for (int i = 0; i < library.size(); i++) {

                if (library.get(i).gameId.equals(game.id)) {
                    vh.inLibrary = true;
                    vh.buttonBook.setImageResource(R.drawable.book);
                }

            }
        }
        if(wishlist != null) {
            for (int i = 0; i < wishlist.size(); i++) {

                if (wishlist.get(i).gameId.equals(game.id)) {
                    vh.inWishlist = true;
                    vh.buttonWishlist.setImageResource(R.drawable.wishlist);
                }

            }
        }


        vh.buttonBook.setVisibility(View.VISIBLE);
        vh.buttonWishlist.setVisibility(View.VISIBLE);



    }
}

