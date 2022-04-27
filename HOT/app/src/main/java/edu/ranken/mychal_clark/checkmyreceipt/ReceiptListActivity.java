package edu.ranken.mychal_clark.checkmyreceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import edu.ranken.mychal_clark.checkmyreceipt.ui.ItemListViewModel;
import edu.ranken.mychal_clark.checkmyreceipt.ui.ReceiptListViewModel;

public class ReceiptListActivity extends AppCompatActivity {

    private FloatingActionButton addReceiptBtn;
    private CreateReceiptDialog createReceiptDialog;
    private ReceiptListViewModel model;
    private TextView errorMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_list);

        addReceiptBtn = findViewById(R.id.listAddBtn);
        errorMessage = findViewById(R.id.receiptListError);
        model = new ReceiptListViewModel();


        model.getErrorMessage().observe(this,(messageId)->{

            if(messageId != null){
                errorMessage.setText(messageId);
                errorMessage.setVisibility(View.VISIBLE);

            }else{
                errorMessage.setText(null);
                errorMessage.setVisibility(View.GONE);
            }

        });
        addReceiptBtn.setOnClickListener((view) -> {
             createReceiptDialog = new CreateReceiptDialog(
                ReceiptListActivity.this,
                "Create Receipt",
                null,
                 (which) ->{
                    model.createReceipt(which, (String id)->{
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
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}