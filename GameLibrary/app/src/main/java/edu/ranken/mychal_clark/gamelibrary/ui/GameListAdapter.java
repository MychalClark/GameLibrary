package edu.ranken.mychal_clark.gamelibrary.ui;

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

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Game;

public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {


    private static final String LOG_TAG = "GameListAdapter";
    private final AppCompatActivity context;
    private final Picasso picasso;
    private final LayoutInflater layoutInflater;
    private List<Game> items;

    public GameListAdapter(AppCompatActivity context, List<Game> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.picasso = Picasso.get();
    }

    public void setItems(List<Game> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (items == null) {
            return 0;
        } else {
            return items.size();
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
            itemView.findViewById(R.id.gameConsole2)
        };


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder vh, int position) {
        Game item = items.get(position);
        vh.gameNameText.setText(item.name);
        vh.gameDescriptionText.setText(item.description);
        vh.gameReleaseYearText.setText(String.valueOf(item.releaseYear));
        vh.gameImage.setImageResource(R.drawable.no_image);
        vh.buttonBook.setImageResource(R.drawable.bookoutline);
        vh.buttonWishlist.setImageResource(R.drawable.wishlistoutline);


        // If statements

        //Picture Setter
        if (item.gameImage == null || item.gameImage.length() == 0 || item.gameImage.isEmpty()) {
            vh.gameImage.setImageResource(R.drawable.no_image);
        } else {
            vh.gameImage.setImageResource(R.drawable.no_image);
            this.picasso
                .load(item.gameImage)
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.no_image)
                .resize(100, 100)
                .centerInside()
                .into(vh.gameImage);
        }


        //Icon Setter

        if (item.consoles == null) {
            for (int i = 0; i < vh.consoleIcons.length; ++i) {
                vh.consoleIcons[i].setImageResource(0);
                vh.consoleIcons[i].setVisibility(View.GONE);
            }
        } else {

            int iconIndex = 0;

            for (Map.Entry<String, Boolean> entry : item.consoles.entrySet()) {

                if (Objects.equals(entry.getValue(), Boolean.TRUE)) {
                    vh.consoleIcons[0].setVisibility(View.VISIBLE);

                    switch (entry.getKey()) {

                        default:
                            vh.consoleIcons[iconIndex++].setImageResource((R.drawable.ic_error));
                            break;
                        case "windows":
                            vh.consoleIcons[iconIndex++].setImageResource((R.drawable.ic_windows));
                            break;
                        case "ps4":
                            vh.consoleIcons[iconIndex++].setImageResource((R.drawable.ic_ps));
                            break;
                    }

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
    }


}

