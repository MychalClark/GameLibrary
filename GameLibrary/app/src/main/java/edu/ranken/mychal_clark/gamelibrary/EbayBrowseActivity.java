package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.ebay.EbayBrowseViewModel;
import edu.ranken.mychal_clark.gamelibrary.ebay.EbayItemListAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.utils.SpinnerOption;

public class EbayBrowseActivity extends AppCompatActivity {
private static final String LOG_TAG = EbayBrowseActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private EbayItemListAdapter adapter;
    private EbayBrowseViewModel model;
    public static final String EXTRA_GAME_ID = "gameId";
    private Spinner consoleSpinner;
    private Switch priceSwitchBtn;
    private Switch soldSwitchBtn;
    private TextView labelPriceSwitch;

    private String gameId;
    private ArrayAdapter<SpinnerOption<String>> consolesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebay_browse);

        //tie views
        priceSwitchBtn = findViewById(R.id.ebayPriceSwitch);
        labelPriceSwitch = findViewById(R.id.labelPriceSwitch);
        labelPriceSwitch.setText(R.string.relevance);

        // get intent
        Intent intent = getIntent();
        gameId = intent.getStringExtra(EXTRA_GAME_ID);

        Log.i(LOG_TAG, gameId);

        //set views
        recyclerView = findViewById(R.id.ebayItemRecycler);
        //create adapter
        adapter = new EbayItemListAdapter(this, null);

        // setup recycler view
        int columns = getResources().getInteger(R.integer.ebayListColumns);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        recyclerView.setAdapter(adapter);

        // bind model

        model = new ViewModelProvider(this).get(EbayBrowseViewModel.class);
        model.fetchGame(gameId);
        model.getSearchResponse().observe(this, (searchResponse)->{
            adapter.setItems(searchResponse);
        });

        //create console Spinner

        consoleSpinner = findViewById(R.id.ebayConsoleSpinner);

        model.getConsoles().observe(this, (consoles) -> {
            if (consoles != null) {

                int selectedPosition = 0;
                String selectedId = model.getFilterConsoleId();

                ArrayList<SpinnerOption<String>> consoleNames = new ArrayList<>(consoles.size());
                consoleNames.add(new SpinnerOption<>(getString(R.string.allConsoles), null));

                for (int i = 0; i < consoles.size(); ++i) {
                    Consoles console = consoles.get(i);

                    if (console.id != null && console.name != null) {
                        consoleNames.add(new SpinnerOption<>(console.name, console.id));
                        if (Objects.equals(console.id, selectedId)) {
                            selectedPosition = i + 1;
                        }
                    }
                }

                consolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, consoleNames);
                consoleSpinner.setAdapter(consolesAdapter);
                consoleSpinner.setSelection(selectedPosition, false);
            }
        });

        //Register Listeners
        consoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<String> option = (SpinnerOption<String>) parent.getItemAtPosition(position) ;
                if(position == 0){model.search("");
                }else{model.search(option.getValue());}

                Log.i(LOG_TAG, "Filter by Console: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        priceSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    model.fetchSort("price");
                    Log.i(LOG_TAG, "price button On");
                    labelPriceSwitch.setText(R.string.price);
                } else{
                    model.fetchSort("newlyListed");
                    Log.i(LOG_TAG, "price button Off");
                labelPriceSwitch.setText(R.string.relevance);}


            }
            });


            //model
    }


}