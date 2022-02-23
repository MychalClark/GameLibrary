package edu.ranken.mychal_clark.gamelibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.ui.GameListAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.GameListModel;

public class MainActivity extends AppCompatActivity {



//views
    private RecyclerView recyclerView;
    private Spinner consoleSpinner;
    private Spinner listSpinner;

    //states
    private GameListAdapter gamesAdapter;
    private GameListModel model;

    private ArrayAdapter<String> consolesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //find views
        recyclerView = findViewById(R.id.gameList);
        consoleSpinner = findViewById(R.id.consoleSpinner);
        consoleSpinner = findViewById(R.id.listSpinner);

        //create adapter
        gamesAdapter = new GameListAdapter(this, null);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gamesAdapter);


       // Test if works
//        ArrayList<Game> newGame =  new ArrayList<Game>();
//        newGame.add(new Game("name","something", 2018));
//        newGame.add(new Game("war","some",2014));
//adapter.setItems(newGame);

//bind Model
        model = new ViewModelProvider(this).get(GameListModel.class);
        model.getGames().observe(this, (games) -> {
            gamesAdapter.setItems(games);
        });

        model.getConsoles().observe(this, (consoles)-> {
            if(consoles !=null) {
                ArrayList<String> consoleNames = new ArrayList<>(consoles.size());
                consoleNames.add(getString(R.string.allConsoles));
                for (Consoles console : consoles){
                    consoleNames.add(console.name);
                }
                consolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, consoles);
                consoleSpinner.setAdapter(consolesAdapter);
            }

            });

        //Register Listeners
        consoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String consoleName = (String) parent.getItemAtPosition(position)
                    model.filterGamesByConsole(consoleName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//do Nothing my guy
            }
        });




    }
}