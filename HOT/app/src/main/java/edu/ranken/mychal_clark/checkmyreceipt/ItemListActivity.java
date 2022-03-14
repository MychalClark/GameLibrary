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

import edu.ranken.mychal_clark.checkmyreceipt.ui.ItemListViewModel;
import edu.ranken.mychal_clark.checkmyreceipt.ui.ReceiptItemListAdapter;

public class ItemListActivity extends AppCompatActivity {

    //crete Views
    private ImageButton fabAdd;
    private Button calcBtn;
    private ItemListViewModel model;
    private RecyclerView recyclerView;
    private ReceiptItemListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Link views
        recyclerView = findViewById(R.id.itemList);
        fabAdd = findViewById(R.id.fabAdd);
        calcBtn = findViewById(R.id.calcBtn);


        //Create Adapter
        adapter = new ReceiptItemListAdapter(this, null, model);

        //Set Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // bind model
        model = new ViewModelProvider(this).get(ItemListViewModel.class);

        model.getReceipt().observe(this,(receipt) -> {
            if (receipt != null){
                Log.i("ll", "soigoisdjgiodsjgoisoigdoigiosdj");
                model.getReceiptId(receipt.receiptId);
            }
        });
        model.getReceiptItems().observe(this,(receiptItems) -> {adapter.setItems(receiptItems);});


        //Set Listeners
        fabAdd.setOnClickListener((view) -> {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
        });

        calcBtn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
        });
    }

}