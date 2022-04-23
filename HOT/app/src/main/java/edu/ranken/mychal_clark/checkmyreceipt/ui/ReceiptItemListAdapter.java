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

        if (model != null) {
            Log.i(LOG_TAG, "hello");
        }
        ;
        if (model == null) {
            Log.i(LOG_TAG, "goodbye");
        }
        ;
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

    // FIXME: (FIXED) total cannot be null, so use "double"
    public double itemTotal() {
        // FIXME: (FIXED) total cannot be null, so use "double"
        double total = 0.00;
        if (items != null) {
            Log.i(LOG_TAG, "yup");
            for (int i = 0; i < items.size(); i++) {
                // FIXME: handle null fields
                Double price = items.get(i).price;
                Integer quantity = items.get(i).quantity;
                Double discount = items.get(i).discountPercent;
                total += (price * quantity) * (1 - discount / 100);
            }

        }
        return total;
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


        // FIXME: (FIXED) indentation
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
        NumberFormat nm = NumberFormat.getNumberInstance();

        if (item.quantity != null) {
            // FIXME: use NumberFormat.getIntegerInstance()
            vh.itemQuantityText.setText(Integer.toString(item.quantity));
        } else {
            vh.itemQuantityText.setText("null");
        }

        if (item.discountPercent != null) {
            // FIXME: use NumberFormat.getPercentInstance()
            vh.itemDiscountText.setText("%" + nm.format(item.discountPercent));
        } else {
            vh.itemQuantityText.setText("null");
        }

        if (item.price != null) {
            vh.itemPriceText.setText(NumberFormat.getCurrencyInstance().format(item.price));
        } else {
            vh.itemPriceText.setText("null");
        }

        // FIXME: handle nulls
        Double total = (item.price * item.quantity) * (1 - item.discountPercent / 100);
        vh.itemTotalPriceText.setText(NumberFormat.getCurrencyInstance().format(total));

        // FIXME: do not save this total to an instance variable!!!
        this.total = total;


    }


}
