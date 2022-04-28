package edu.ranken.mychal_clark.checkmyreceipt.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.ranken.mychal_clark.checkmyreceipt.CreateReceiptDialog;
import edu.ranken.mychal_clark.checkmyreceipt.ItemListActivity;
import edu.ranken.mychal_clark.checkmyreceipt.R;
import edu.ranken.mychal_clark.checkmyreceipt.ReceiptListActivity;
import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;

public class ReceiptListAdapter extends RecyclerView.Adapter<ReceiptListViewHolder> {

    private final String LOG_TAG = ReceiptListAdapter.class.getSimpleName();
    private final LayoutInflater layoutInflater;
    private final ReceiptListViewModel model;
    private List<Receipt> items;


    public ReceiptListAdapter(AppCompatActivity context, ReceiptListViewModel model) {
        this.model = model;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setItems(List<Receipt> items) {
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
    public ReceiptListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.receipt_list_item, parent, false);

        ReceiptListViewHolder vh = new ReceiptListViewHolder(itemView);
       vh.companyNameLabel = itemView.findViewById(R.id.receiptNameText);
        vh.createdDateLabel = itemView.findViewById(R.id.receiptDateText);
        vh.totalPriceLabel = itemView.findViewById(R.id.receiptTotalText);

        vh.itemView.setOnClickListener((view) -> {
            Receipt receipt = items.get(vh.getAdapterPosition());
            Intent intent = new Intent(view.getContext(), ItemListActivity.class);
            intent.putExtra(ItemListActivity.EXTRA_RECEIPT_ID, receipt.receiptId);
            view.getContext().startActivity(intent);
        });

        vh.itemView.setOnLongClickListener((view) -> {
            Receipt receipt = items.get(vh.getAdapterPosition());
                CreateReceiptDialog createReceiptDialog = new CreateReceiptDialog(
                    view.getContext(),
                    "Rename Receipt",
                    null,
                    (which) ->{
                        model.editReceiptName(which, receipt, (String id)->{
                            Intent intent = new Intent(view.getContext(), ItemListActivity.class);
                            intent.putExtra(ItemListActivity.EXTRA_RECEIPT_ID, id);
                            view.getContext().startActivity(intent);
                        });
                    },
                    (which) -> {
                        //nothing happens
                    });
                createReceiptDialog.show();
            return false;
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptListViewHolder vh, int position) {

        Receipt item = items.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if(item.name != null){
            vh.companyNameLabel.setText(item.name);
        }else{
            vh.companyNameLabel.setText(R.string.noName);
        }
        if(item.addedOn != null){
            vh.createdDateLabel.setText(sdf.format(item.addedOn));
        }else{
            vh.createdDateLabel.setText(R.string.noDate);
        }
        if(item.total != null){
            vh.totalPriceLabel.setText(NumberFormat.getCurrencyInstance().format(item.total));
        }else{
            vh.totalPriceLabel.setText(R.string.noPrice);
        }



    }

}
