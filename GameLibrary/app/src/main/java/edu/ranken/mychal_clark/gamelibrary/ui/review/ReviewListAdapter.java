package edu.ranken.mychal_clark.gamelibrary.ui.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewViewHolder>{

    private static final String LOG_TAG = "GameListAdapter";

    private final AppCompatActivity context;
    private final LayoutInflater layoutInflater;
    private List<Review> items;


    public ReviewListAdapter(AppCompatActivity context, List<Review> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
    }

        public void setItems(List<Review> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.review_item, parent, false);

        ReviewViewHolder vh = new ReviewViewHolder(itemView);
        vh.displayName = itemView.findViewById(R.id.usernameText);
        vh.date = itemView.findViewById(R.id.dateText);
        vh.reviewText = itemView.findViewById(R.id.reviewText);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder vh, int position) {

        Review item = items.get(position);

        String pattern = "MM/dd/yyyy HH:mm:ss";

        DateFormat df = new SimpleDateFormat(pattern);

        String reviewDate = df.format(item.date);


        vh.reviewText.setText(item.reviewText);
        vh.displayName.setText(item.displayName);
        vh.date.setText(reviewDate);
    }
}
