package edu.ranken.mychal_clark.checkmyreceipt;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.Objects;

import edu.ranken.mychal_clark.checkmyreceipt.data.ReceiptItem;
import edu.ranken.mychal_clark.checkmyreceipt.ui.AddItemViewModel;

public class AddItemActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AddItemActivity";

    private EditText priceText;
    private EditText quantityText;
    private EditText discountText;
    private TextView errorMessage;
    private ImageButton fabSave;
    private AddItemViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        priceText = findViewById(R.id.addPriceInput);
        discountText = findViewById(R.id.addDiscountInput);
        quantityText = findViewById(R.id.addQuantityInput);
        errorMessage = findViewById(R.id.addItemError);
        fabSave = findViewById(R.id.fabSave);
        model = new ViewModelProvider(this).get(AddItemViewModel.class);

        fabSave.setOnClickListener((view) -> {
            createItem();
        });


        model.getReceiptSaved().observe(this,(finish)->{
            if(Objects.equals(finish,Boolean.TRUE)){

                finish();
            }
        });
        model.getError().observe(this,(error)-> {
            errorMessage.setText(error);
        });
    }

    //Functions
    private void createItem() {
        errorMessage.setVisibility(View.GONE);
        hideKeyboard(this, errorMessage);

        Double discount;
        Double price;
        Integer quantity;

        if (priceText.getText().toString().isEmpty()) {
            errorMessage.setText("Please enter a price");
            errorMessage.setVisibility(View.VISIBLE);
            Log.e(LOG_TAG, "Please Enter a price.");

        } else {
            price = Double.parseDouble(priceText.getText().toString());
            if (!discountText.getText().toString().isEmpty()) {
                discount = Double.parseDouble(discountText.getText().toString());
            } else {
                discount = 0.00;
            }
            if (!quantityText.getText().toString().isEmpty()) {
                quantity = Integer.parseInt(quantityText.getText().toString());
            } else {
                quantity = 1;
            }


            if (quantity != null && discount != null && price != null && price > 0 && quantity > 0 && quantity < 1001 && discount >= 0 && discount <= 100) {
                ReceiptItem newItem = new ReceiptItem();
                newItem.price = price;
                newItem.discountPercent = discount;
                newItem.quantity = quantity;
                newItem.addedOn = new Date();
                model.addItem(newItem);

            } else {
                errorMessage.setVisibility(View.VISIBLE);
                Log.e(LOG_TAG, "Please Fix inputs.");
            }
        }


    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //OverRides

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

}