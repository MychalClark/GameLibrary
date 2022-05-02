package edu.ranken.mychal_clark.checkmyreceipt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;

import edu.ranken.mychal_clark.checkmyreceipt.ui.ItemListViewModel;
import edu.ranken.mychal_clark.checkmyreceipt.ui.ReceiptItemListAdapter;
import edu.ranken.mychal_clark.checkmyreceipt.ui.TotalsViewModel;

public class ItemListActivity extends AppCompatActivity {

    public static final String EXTRA_RECEIPT_ID = "receiptId";
    public static final String LOG_TAG = ItemListActivity.class.getSimpleName();

    //crete Views
    private ImageButton fabAdd;
    private ImageButton fabDelete;
    private Button calcBtn;
    private ItemListViewModel model;
    private RecyclerView recyclerView;
    private ReceiptItemListAdapter adapter;
    private TextView subtotal;
    //private TotalsViewModel totalModel;  // FIXME: do not use view model here
    private String receiptId;
    private TextView receiptError;
    private TextView itemError;
    private double total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_item_list);


        //Link views
        recyclerView = findViewById(R.id.itemList);
        fabAdd = findViewById(R.id.fabAdd);
        calcBtn = findViewById(R.id.calcBtn);
        fabDelete = findViewById(R.id.fabDelete);
        subtotal = findViewById(R.id.subTotalText);
        itemError = findViewById(R.id.itemListItemError);
        receiptError = findViewById(R.id.itemListReceiptError);

        // get intent
        Intent intentReceipt = getIntent();
        receiptId = intentReceipt.getStringExtra(EXTRA_RECEIPT_ID);

        Log.i(LOG_TAG, receiptId);

// bind model
        model = new ViewModelProvider(this).get(ItemListViewModel.class);
        //totalModel = new ViewModelProvider(this).get(TotalsViewModel.class);

        //Create Adapter
        adapter = new ReceiptItemListAdapter(this, null, model);

        //Set Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        model.fetchReceipt(receiptId);
        //totalModel.fetchReceipt(receiptId);

        model.getMessageReceipt().observe(this,(error)->{
            if (error != null) {
                receiptError.setText(error);
                receiptError.setVisibility(View.VISIBLE);
            } else {
                // FIXME: set text
                receiptError.setVisibility(View.GONE);
            }
        });
        model.getMessageReceiptItems().observe(this,(error)->{
            if (error != null) {
                itemError.setText(error);
                itemError.setVisibility(View.VISIBLE);
            } else {
                // FIXME: set text
                itemError.setVisibility(View.GONE);
            }
        });

        // FIXME: dead code
        model.getReceipt().observe(this, (receipt) -> {
            if (receipt != null) {
            }
        });

        model.getReceiptItems().observe(this, (receiptItems) -> {
            adapter.setItems(receiptItems);

            total = 0.00;
            if(receiptItems != null) {
                // FIXME: use enhanced for loop
                for (int i = 0; i < receiptItems.size(); i++) {
                    total += receiptItems.get(i).itemTotal;
                }
                subtotal.setText(NumberFormat.getCurrencyInstance().format(total));
                model.updateSubtotal(total);
            } else {
                // FIXME: unhandled case
            }

            ;
        });


        //Set Listeners
        fabAdd.setOnClickListener((view) -> {
            // FIXME: this is a kludge, and does not address underlying bugs
            //        code has no effect at best, and causes unpredictable behavior in general
            // model.updateSubtotal(total);

            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra(ItemListActivity.EXTRA_RECEIPT_ID, receiptId);
            startActivity(intent);
        });

        fabDelete.setOnClickListener((view) -> {

            model.deleteAllItems();

            // FIXME: this recalculates the subtotal before the items are deleted
            // model.updateSubtotal(total);
        });


        calcBtn.setOnClickListener((view) -> {
            if(model.getReceipt().getValue() != null){
                // FIXME: this is a kludge, and does not address underlying bugs
                // totalModel.setSalesTax(model.getReceipt().getValue().taxPercent);

            Intent intent = new Intent(this, TotalsActivity.class);
            intent.putExtra(TotalsActivity.EXTRA_RECEIPT_ID, receiptId);
            startActivity(intent);}
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as back navigation
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("receiptId", receiptId);
    }


}