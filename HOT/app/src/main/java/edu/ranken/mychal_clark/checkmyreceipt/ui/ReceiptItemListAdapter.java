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
import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ReceiptItemListAdapter extends RecyclerView.Adapter<ReceiptItemViewHolder> {

    private static final String LOG_TAG = "ReceiptItemListAdapter";

    private final LayoutInflater layoutInflater;
    private final AppCompatActivity context;
    private List<ReceiptItem> items;
    private final ItemListViewModel model;

    public ReceiptItemListAdapter(AppCompatActivity context, List<ReceiptItem> items, ItemListViewModel model) {
        this.context = context;
        this.items = items;
        this.layoutInflater = LayoutInflater.from(context);
        this.model = model;
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

        vh.itemDeleteBtn.setOnClickListener((view) -> {
            ReceiptItem item = items.get(vh.getAdapterPosition());
           String itemId =  item.receiptItemId;
            model.deleteItem(itemId);
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptItemViewHolder vh, int position) {

        ReceiptItem item = items.get(position);
        NumberFormat nm = NumberFormat.getNumberInstance();

        if (item.quantity != null) {
           vh.itemQuantityText.setText(Integer.toString(item.quantity));
        } else {
            vh.itemQuantityText.setText("null");
        }

        if (item.discountPercent != null) {
            vh.itemDiscountText.setText(nm.format(item.discountPercent));
        } else {
            vh.itemQuantityText.setText("null");
        }

        if (item.price != null) {
            vh.itemPriceText.setText(nm.format(item.price));
        } else {
            vh.itemPriceText.setText("null");
        }



    }


}
