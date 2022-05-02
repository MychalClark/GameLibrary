package edu.ranken.mychal_clark.checkmyreceipt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public class CreateReceiptDialog {
    private final AlertDialog dialog;
    private final EditText receiptName;
    private String storeName;

    public CreateReceiptDialog(
        @NonNull Context context,
        @NonNull CharSequence dialogTitle,
        @Nullable String storeName,
        @NonNull OnClickListener onOk,
        @Nullable OnClickListener onCancel) {  // FIXME: create a different interface for the cancel event

        //get inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate the layout
        View contentView = inflater.inflate(R.layout.receipt_dialog, null, false);
        //Find views
        receiptName = contentView.findViewById(R.id.nameReceiptInput);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(dialogTitle);
        builder.setView(contentView);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            this.storeName = receiptName.getText().toString();
            onOk.onClick(this.storeName);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            // FIXME: this.storeName is always null here
            if (onCancel != null) { onCancel.onClick(this.storeName); }
        });
        dialog = builder.create();


    }
    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }
    public interface OnClickListener {
        void onClick(String StoreName);
    }
}
