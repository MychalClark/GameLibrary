package edu.ranken.mychal_clark.checkmyreceipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.protobuf.StringValue;

import java.text.NumberFormat;

import edu.ranken.mychal_clark.checkmyreceipt.data.Receipt;
import edu.ranken.mychal_clark.checkmyreceipt.ui.ItemListViewModel;
import edu.ranken.mychal_clark.checkmyreceipt.ui.ReceiptItemListAdapter;
import edu.ranken.mychal_clark.checkmyreceipt.ui.TotalsViewModel;

public class ItemListActivity extends AppCompatActivity {

    //crete Views
    private ImageButton fabAdd;
    private ImageButton fabDelete;
    private Button calcBtn;
    private ItemListViewModel model;
    private RecyclerView recyclerView;
    private ReceiptItemListAdapter adapter;
    private TextView subtotal;
    private TotalsViewModel totalModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Link views
        recyclerView = findViewById(R.id.itemList);
        fabAdd = findViewById(R.id.fabAdd);
        calcBtn = findViewById(R.id.calcBtn);
        fabDelete = findViewById(R.id.fabDelete);
        subtotal = findViewById(R.id.subTotalText);

// bind model
       model = new ViewModelProvider(this).get(ItemListViewModel.class);
        totalModel = new ViewModelProvider(this).get(TotalsViewModel.class);

        //Create Adapter
        adapter = new ReceiptItemListAdapter(this, null, model);

        //Set Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);





        model.getReceipt().observe(this,(receipt) -> {
            if (receipt != null){
                model.getReceiptId(receipt.receiptId);

                // FIXME: show the subtotal, don't update the receipt
                //        this causes an infinite loop of updates
                model.updateSubtotal(adapter.itemTotal());
            }
        });

        // FIXME: indentation and code style
        model.getReceiptItems().observe(this,(receiptItems) -> {adapter.setItems(receiptItems);
           subtotal.setText(NumberFormat.getCurrencyInstance().format(adapter.itemTotal()));

           // FIXME: update receipt subtotal here instead

           ;});

        // FIXME: observe and show error messages


        //Set Listeners
        fabAdd.setOnClickListener((view) -> {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);

            // FIXME: items have not been updated yet
            //        this recalculates the subtotal before the item is added
            model.updateSubtotal(adapter.itemTotal());
        });

        fabDelete.setOnClickListener((view) -> {
            model.deleteAllItems();

            // FIXME: items have not been updated yet
            //        this recalculates the subtotal before the items are deleted
            model.updateSubtotal(adapter.itemTotal());
        });


        calcBtn.setOnClickListener((view) -> {
            // FIXME: this crashes when receipt or taxPercent are null
            totalModel.setSalesTax(model.getReceipt().getValue().taxPercent);

            Intent intent = new Intent(this, TotalsActivity.class);
            startActivity(intent);
        });


    }


}