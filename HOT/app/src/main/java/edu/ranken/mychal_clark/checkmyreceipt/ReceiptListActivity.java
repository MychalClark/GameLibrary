package edu.ranken.mychal_clark.checkmyreceipt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import edu.ranken.mychal_clark.checkmyreceipt.ui.ReceiptListAdapter;
import edu.ranken.mychal_clark.checkmyreceipt.ui.ReceiptListViewModel;

public class ReceiptListActivity extends AppCompatActivity {

    private FloatingActionButton addReceiptBtn;
    private CreateReceiptDialog createReceiptDialog;
    private ReceiptListViewModel model;
    private TextView errorMessage;
    private ReceiptListAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_list);


        recyclerView = findViewById(R.id.receiptRecycler);
        addReceiptBtn = findViewById(R.id.listAddBtn);
        errorMessage = findViewById(R.id.receiptListError);
        model = new ReceiptListViewModel();

        //Create Adapter
        adapter = new ReceiptListAdapter(this, model);

        //Set Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        model.getReceipts().observe(this, (receipts) -> {
            adapter.setItems(receipts);
        });

        model.getErrorMessage().observe(this, (messageId) -> {

            if (messageId != null) {
                errorMessage.setText(messageId);
                errorMessage.setVisibility(View.VISIBLE);

            } else {
                errorMessage.setText(null);
                errorMessage.setVisibility(View.GONE);
            }

        });
        addReceiptBtn.setOnClickListener((view) -> {
            createReceiptDialog = new CreateReceiptDialog(
                ReceiptListActivity.this,
                "Create Receipt",
                null,
                (which) -> {
                    model.createReceipt(which, (String id) -> {
                        Intent intent = new Intent(this, ItemListActivity.class);
                        intent.putExtra(ItemListActivity.EXTRA_RECEIPT_ID, id);
                        this.startActivity(intent);
                    });
                },
                (which) -> {
                    //nothing happens
                });
            createReceiptDialog.show();
        });


    }

    //Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as temporal navigation
            onBackPressed();
            return true;
        } else if (itemId == R.id.actionSignOut) {
            // FIXME: finish the activity when the user is signed out
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}