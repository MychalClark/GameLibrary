package edu.ranken.mychal_clark.checkmyreceipt;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.NumberFormat;

import edu.ranken.mychal_clark.checkmyreceipt.ui.TotalsViewModel;

public class TotalsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Totals Activity";

    private EditText salesTax;
    private TextView tax;
    private TextView tip10;
    private TextView tip20;
    private TextView tip30;
    private TextView total;
    private TextView subTotal;
    private TotalsViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totals);

        salesTax = findViewById(R.id.salesTaxInput);
        tax = findViewById(R.id.taxValue);
        tip10 = findViewById(R.id.tip10Value);
        tip20 = findViewById(R.id.tip20Value);
        tip30 = findViewById(R.id.tip30Value);
        total = findViewById(R.id.totalValue);
        subTotal = findViewById(R.id.subtotalValue);
        model = new ViewModelProvider(this).get(TotalsViewModel.class);

        model.getReceipt().observe(this, (receipt) -> {
            if (receipt != null) {
                // FIXME: update salesTax field
                // FIXME: handle nulls
                // FIXME: reuse NumberFormat object
                tax.setText(NumberFormat.getCurrencyInstance().format(receipt.taxAmount));
                tip10.setText(NumberFormat.getCurrencyInstance().format(receipt.tip10));
                tip20.setText(NumberFormat.getCurrencyInstance().format(receipt.tip20));
                tip30.setText(NumberFormat.getCurrencyInstance().format(receipt.tip30));
                total.setText(NumberFormat.getCurrencyInstance().format(receipt.total));
                subTotal.setText(NumberFormat.getCurrencyInstance().format(receipt.subtotal));
            }
        });

        salesTax.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // FIXME: handle exceptions
                Double saleTaxNum;
                if (salesTax.getText().toString().isEmpty()) {
                    saleTaxNum = 0.00;
                    model.setSalesTax(saleTaxNum);
                }
                else if(Double.parseDouble(salesTax.getText().toString()) < 0 || Double.parseDouble(salesTax.getText().toString()) > 100 ){
                    // FIXME: set error message
                    model.getErrorSalesTax();
                }
                else {
                    saleTaxNum = (Double.parseDouble(salesTax.getText().toString()));
                    model.setSalesTax(saleTaxNum);
                }

                // FIXME: return true, to signal event handled
                return false;
            }

            // FIXME: return false, to signal event not handled
            return true;
        });

        // FIXME: remove dead code
//        salesTax.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if(i == EditorInfo.IME_ACTION_DONE){
//                Toast.makeText(getApplicationContext(),"Done pressed",Toast.LENGTH_SHORT).show();
//            }
//            return false;
//        });
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