package edu.ranken.mychal_clark.gamelibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConsoleChooserDialog {

    private final AlertDialog dialog;
    private final Map<String,Boolean> selectedConsoles;
    private final Map<String,Boolean> supportedConsoles;

    public ConsoleChooserDialog(
        @NonNull Context context,
        @NonNull CharSequence title,
        @Nullable Map<String,Boolean> supportedConsoles,
        @Nullable Map<String,Boolean> selectedConsoles,
        OnChooseListener onChoose
    ) {

        //get inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the layout
        View contentView = inflater.inflate(R.layout.console_chooser_dialog, null, false);

        //Find views
        ImageButton xboxBtn = contentView.findViewById(R.id.xboxBtn);
        ImageButton nintendoBtn = contentView.findViewById(R.id.nintendoBtn);
        ImageButton playstationBtn = contentView.findViewById(R.id.playstationBtn);
        ImageButton windowsBtn = contentView.findViewById(R.id.windowsBtn);

        //supported
        if(supportedConsoles == null){
            this.supportedConsoles = null;
        }else{
            this.supportedConsoles = supportedConsoles;

            Boolean xbox = Objects.equals(supportedConsoles.get("xbox"), Boolean.TRUE);
            xboxBtn.setEnabled(xbox);

            Boolean nintendo = Objects.equals(supportedConsoles.get("nintendo"), Boolean.TRUE);
            nintendoBtn.setEnabled(nintendo);

            Boolean playstation = Objects.equals(supportedConsoles.get("playstation"), Boolean.TRUE);
            playstationBtn.setEnabled(playstation);

            Boolean windows = Objects.equals(supportedConsoles.get("windows"), Boolean.TRUE);
            windowsBtn.setEnabled(windows);


        }

        //selected
        if(selectedConsoles == null){
            this.selectedConsoles = new HashMap<>();
        }else{
            this.selectedConsoles = selectedConsoles;

           boolean xbox = Objects.equals(selectedConsoles.get("xbox"), Boolean.TRUE);
            boolean windows = Objects.equals(selectedConsoles.get("windows"), Boolean.TRUE);
            boolean playstation = Objects.equals(selectedConsoles.get("playstation"), Boolean.TRUE);
            boolean nintendo = Objects.equals(selectedConsoles.get("nintendo"), Boolean.TRUE);



           //... other consoles

        }

        //register listeners
        xboxBtn.setOnClickListener((view) ->{
            boolean checked = Objects.equals(this.selectedConsoles.get("xbox"), Boolean.TRUE);
            this.selectedConsoles.put("xbox", checked);

            xboxBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#01E979")));

            //xboxBtn.setImageResource();
        });
        nintendoBtn.setOnClickListener((view) ->{
            boolean checked = Objects.equals(this.selectedConsoles.get("nintendo"), Boolean.TRUE);
            this.selectedConsoles.put("nintendo", checked);

            nintendoBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#01E979")));

            //xboxBtn.setImageResource();
        });
        playstationBtn.setOnClickListener((view) ->{
            boolean checked = Objects.equals(this.selectedConsoles.get("playstation"), Boolean.TRUE);
            this.selectedConsoles.put("playstation", checked);

            playstationBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#01E979")));

            //xboxBtn.setImageResource();
        });
        windowsBtn.setOnClickListener((view) ->{
            boolean checked = !Objects.equals(this.selectedConsoles.get("windows"), Boolean.TRUE);
            this.selectedConsoles.put("windows", checked);

            windowsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#01E979")));

            //xboxBtn.setImageResource();
        });
        // ... other consoles;


        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(contentView);
        builder.setPositiveButton(android.R.string.ok, (dialog, which)->{
            onChoose.onChoose(this.selectedConsoles);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which)->{
            // Do nothing
        });
        dialog = builder.create();

    }

    public void show() { dialog.show(); }
    public void cancel() { dialog.cancel(); }

    public interface OnChooseListener{
        void onChoose(@NonNull Map<String,Boolean> selectedConsoles);
    }
}
