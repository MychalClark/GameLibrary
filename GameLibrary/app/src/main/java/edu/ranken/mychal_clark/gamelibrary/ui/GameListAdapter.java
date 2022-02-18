package edu.ranken.mychal_clark.gamelibrary.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Game;

public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {


    private static final String LOG_TAG = "GameListAdapter";
    private final AppCompatActivity context;
    private final LayoutInflater layoutInflater;
    private List<Game> items;

    public GameListAdapter(AppCompatActivity context, List<Game> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
    }
    public void setItems(List<Game> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if(items == null){
            return 0;
        }
        else {
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


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder vh, int position) {
        Game item = items.get(position);
        vh.gameNameText.setText(item.name);
        vh.gameDescriptionText.setText(item.description);
        vh.gameReleaseYearText.setText(String.valueOf(item.releaseYear));
        vh.gameImage.setImageResource(R.drawable.no_image);

        // If statements

        if (item.image == null || item.image.length() == 0) {
            vh.gameImage.setImageResource(R.drawable.no_image);
  }
//        else {
//            vh.gameImage.setImageResource(R.drawable.no_image);
//            this.picasso
//                .load(item.image)
//                .noPlaceholder()
//                //.placeholder(R.drawable.ic_downloading)
//                .error(R.drawable.no_image)
//                .resize(200, 300)
//                .centerInside()
//                .into(vh.gameImage);
//        }



    }
}
