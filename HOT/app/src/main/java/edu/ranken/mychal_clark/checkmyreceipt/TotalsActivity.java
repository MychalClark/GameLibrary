package edu.ranken.mychal_clark.checkmyreceipt;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.NumberFormat;

import edu.ranken.mychal_clark.checkmyreceipt.ui.TotalsViewModel;

public class TotalsActivity extends AppCompatActivity {

    private static final String LOG_TAG = TotalsActivity.class.getSimpleName();
    public static final String EXTRA_RECEIPT_ID = "receiptId";

    private EditText salesTax;
    private TextView tax;
    private TextView tip10;
    private TextView tip20;
    private TextView tip30;
    private TextView total;
    private TextView subTotal;
    private TotalsViewModel model;
    private TextView receiptError;
    private TextView salesTaxError;
    private String receiptId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_totals);

        salesTax = findViewById(R.id.salesTaxInput);
        tax = findViewById(R.id.taxValue);
        tip10 = findViewById(R.id.tip10Value);
        tip20 = findViewById(R.id.tip20Value);
        tip30 = findViewById(R.id.tip30Value);
        total = findViewById(R.id.totalValue);
        subTotal = findViewById(R.id.subtotalValue);
        model = new ViewModelProvider(this).get(TotalsViewModel.class);
        salesTaxError = findViewById(R.id.totalTaxErrorText);
        receiptError = findViewById(R.id.totalReceiptErrorText);

        // get intent
        Intent intentReceipt = getIntent();
        receiptId = intentReceipt.getStringExtra(EXTRA_RECEIPT_ID);


        model.fetchReceipt(receiptId);
        model.getErrorSalesTax().observe(this, (error) -> {
            if (error != null) {
                salesTaxError.setText(error);
                salesTaxError.setVisibility(View.VISIBLE);
            } else {
                // FIXME: set text
                salesTaxError.setVisibility(View.GONE);
            }
        });
        model.getErrorReceipt().observe(this, (error) -> {
            if (error != null) {
                receiptError.setText(error);
                receiptError.setVisibility(View.VISIBLE);
            } else {
                // FIXME: set text
                receiptError.setVisibility(View.GONE);
            }
        });
        model.getReceipt().observe(this, (receipt) -> {
            if (receipt != null) {
                // FIXME: show taxPercent in EditText
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                tax.setText(nf.format(receipt.taxAmount));
                tip10.setText(nf.format(receipt.tip10));
                tip20.setText(nf.format(receipt.tip20));
                tip30.setText(nf.format(receipt.tip30));
                total.setText(nf.format(receipt.total));
                subTotal.setText(nf.format(receipt.subtotal));
            } else {
                // FIXME: unhandled case
            }
        });

        salesTax.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // FIXME: handle NumberFormatException
                //        entering "." crashes the app

                Double saleTaxNum;
                if (salesTax.getText().toString().isEmpty()) {
                    saleTaxNum = 0.00;
                    model.setSalesTax(saleTaxNum);
                    model.clearMessages();  // FIXME: don't do this here, this overrides setSalesTax

                // FIXME: range should be 0-20 per requirements
                } else if (Double.parseDouble(salesTax.getText().toString()) < 0 || Double.parseDouble(salesTax.getText().toString()) > 100) {
                    model.postSaleTaxError(R.string.taxBetweenError);
                } else {
                    saleTaxNum = (Double.parseDouble(salesTax.getText().toString()));
                    model.setSalesTax(saleTaxNum);
                    model.clearMessages();  // FIXME: don't do this here, this overrides setSalesTax
                }

                return true;
            }

            return false;
        });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("receiptId", receiptId);
        // FIXME: save entered taxPercent
    }


}