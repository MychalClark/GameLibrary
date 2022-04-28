package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.R;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ReceiptItemListAdapter extends RecyclerView.Adapter<ReceiptItemViewHolder> {

    private static final String LOG_TAG = "ReceiptItemListAdapter";

    private final LayoutInflater layoutInflater;
    private final ItemListViewModel model;
    private List<ReceiptItem> items;
    private Double total;


    public ReceiptItemListAdapter(AppCompatActivity context, List<ReceiptItem> items, ItemListViewModel model) {
        this.model = model;
        this.items = items;
        this.layoutInflater = LayoutInflater.from(context);
    }


    public void setItems(List<ReceiptItem> items) {
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
    public ReceiptItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.receipt_item, parent, false);

        ReceiptItemViewHolder vh = new ReceiptItemViewHolder(itemView);
        vh.itemDiscountText = itemView.findViewById(R.id.itemDiscountText);
        vh.itemPriceText = itemView.findViewById(R.id.itemPriceText);
        vh.itemQuantityText = itemView.findViewById(R.id.itemQuantityText);
        vh.itemDeleteBtn = itemView.findViewById(R.id.itemDeleteBtn);
        vh.itemTotalPriceText = itemView.findViewById(R.id.itemTotalPriceText);


        // FIXME: indentation //fixed//
        vh.itemDeleteBtn.setOnClickListener((view) -> {
            ReceiptItem item = items.get(vh.getAdapterPosition());
            if (model != null) {
                Log.i(LOG_TAG, "hello");
                model.deleteItem(item.receiptItemId);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptItemViewHolder vh, int position) {

        ReceiptItem item = items.get(position);

        if (item.quantity != null) {
            // FIXME: use NumberFormat.getIntegerInstance() //fixed//
            vh.itemQuantityText.setText(NumberFormat.getIntegerInstance().format(item.quantity));
        } else {
            vh.itemQuantityText.setText(R.string.nothingNull);
            item.quantity = 0;
        }

        if (item.discountPercent != null) {
            // FIXME: use NumberFormat.getPercentInstance() //fixed//
            vh.itemDiscountText.setText(NumberFormat.getPercentInstance().format(item.discountPercent / 100));
        } else {
            vh.itemQuantityText.setText(R.string.nothingNull);
            item.discountPercent = 0.00;
        }

        if (item.price != null) {
            vh.itemPriceText.setText(NumberFormat.getCurrencyInstance().format(item.price));
        } else {
            vh.itemPriceText.setText(R.string.nothingNull);
            item.price = 0.00;
        }

        if (item.itemTotal != null) {
            vh.itemTotalPriceText.setText(NumberFormat.getCurrencyInstance().format(item.itemTotal));
        } else {
            vh.itemTotalPriceText.setText(R.string.nothingNull);
            item.itemTotal = 0.00;
        }


        // FIXME: do not save this total to an instance variable!!! //Fixed :O //


    }


}
