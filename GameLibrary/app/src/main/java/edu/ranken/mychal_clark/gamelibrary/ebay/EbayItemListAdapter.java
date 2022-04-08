package edu.ranken.mychal_clark.gamelibrary.ebay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import edu.ranken.mychal_clark.gamelibrary.R;

public class EbayItemListAdapter extends RecyclerView.Adapter<EbayItemViewHolder> {

    private static final String LOG_TAG = EbayItemListAdapter.class.getSimpleName();

    private final AppCompatActivity context;
    private final LayoutInflater layoutInflater;
    private EbayBrowseAPI.SearchResponse searchResponse;
    private final Picasso picasso;

    public EbayItemListAdapter(AppCompatActivity context, EbayBrowseAPI.SearchResponse searchResponse){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.searchResponse = searchResponse;
        this.picasso = Picasso.get();
    }
    public void setItems(EbayBrowseAPI.SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

    if (searchResponse != null && searchResponse.itemSummaries != null) {
            return searchResponse.itemSummaries.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public EbayItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = layoutInflater.inflate(R.layout.ebay_item, parent, false);


        EbayItemViewHolder vh = new EbayItemViewHolder(itemView);
        vh.ebayItemImage = itemView.findViewById(R.id.ebayItemImage);
        vh.ebayItemPrice = itemView.findViewById(R.id.ebayItemPrice);
        vh.ebayItemSeller = itemView.findViewById(R.id.ebayItemSeller);
        vh.ebayItemShipping = itemView.findViewById(R.id.ebayItemShipping);
        vh.ebayItemTitle = itemView.findViewById(R.id.ebayItemTitle);

        vh.itemView.setOnClickListener((view)->{

            EbayBrowseAPI.ItemSummary item = searchResponse.itemSummaries.get(vh.getAdapterPosition());
            Log.i(LOG_TAG, "clicked on this item:" + item.itemId);
            Uri itemWebUri = Uri.parse(item.itemWebUrl);
            context.startActivity(new Intent(Intent.ACTION_VIEW, itemWebUri));

        });

        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EbayItemViewHolder vh, int position) {


            EbayBrowseAPI.ItemSummary item = searchResponse.itemSummaries.get(position);
Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Log.i(LOG_TAG, "hh " + gson.toJson(searchResponse.itemSummaries.get(0).shippingOptions.get(0).shippingCost));
        Log.i(LOG_TAG, "aa " + gson.toJson(item));


            vh.ebayItemTitle.setText(item.title);
            vh.ebayItemPrice.setText(item.price.value +" "+ item.price.currency);
            vh.ebayItemSeller.setText(item.seller.username);

            if(item.shippingOptions != null && item.shippingOptions.size() > 0){
                if(item.shippingOptions.get(0).shippingCost != null) {

                    vh.ebayItemShipping.setText(
                        item.shippingOptions.get(0).shippingCostType + " " +
                            item.shippingOptions.get(0).shippingCost.value + " " +
                            item.shippingOptions.get(0).shippingCost.currency
                    );
                } else {
                    vh.ebayItemShipping.setText(item.shippingOptions.get(0).shippingCostType);
                }
            } else{
                vh.ebayItemShipping.setText(R.string.no_shipping);
            }
//            if(item.shipping != null){
//            vh.ebayItemShipping.setText(item.shipping.shippingCost.get(0).toString());}
//            else{ vh.ebayItemShipping.setText("null");}

            if (item.image.imageUrl == null || item.image.imageUrl.length() == 0) {
                vh.ebayItemImage.setImageResource(R.drawable.no_image);
            } else {
                vh.ebayItemImage.setImageResource(R.drawable.no_image);
                this.picasso
                    .load(item.image.imageUrl)
                    .noPlaceholder()
                    //.placeholder(R.drawable.ic_downloading)
                    .error(R.drawable.no_image)
                    .resize(200, 300)
                    .centerCrop()
                    .into(vh.ebayItemImage);
            }
        }
    }

