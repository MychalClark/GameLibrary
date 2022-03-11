package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.data.GameList;
import edu.ranken.mychal_clark.gamelibrary.ui.GameListAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.GameListModel;
import edu.ranken.mychal_clark.gamelibrary.ui.SpinnerOption;

public class GameListActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GameListActivity";


    //views
    private RecyclerView recyclerView;
    private Spinner consoleSpinner;
    private Spinner listSpinner;


    //states
    private GameListModel model;
    private GameListAdapter gamesAdapter;

    private ArrayAdapter<SpinnerOption<String>> consolesAdapter;
    private ArrayAdapter<SpinnerOption<GameList>> listAdapter;

    //private ArrayAdapter<ConsoleFilter> consolesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //find views

        consoleSpinner = findViewById(R.id.consoleSpinner);
        listSpinner = findViewById(R.id.listSpinner);
        recyclerView = findViewById(R.id.gameList);


        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //recycler and adapter attach
        model = new ViewModelProvider(this).get(GameListModel.class);
        gamesAdapter = new GameListAdapter(this, model);
        recyclerView.setAdapter(gamesAdapter);


        // Test if works
//        ArrayList<Game> newGame =  new ArrayList<Game>();
//        newGame.add(new Game("name","something", 2018));
//        newGame.add(new Game("war","some",2014));
//adapter.setItems(newGame);

//bind Model


        //Populate Spinner
        SpinnerOption<GameList>[] listOptions = new SpinnerOption[]{
            new SpinnerOption<>(getString(R.string.allGames), GameList.ALL_GAMES),
            new SpinnerOption<>(getString(R.string.myLibrary), GameList.LIBRARY),
            new SpinnerOption<>(getString(R.string.myWishlist), GameList.WISHLIST),
            new SpinnerOption<>(getString(R.string.myLibraryWishlist), GameList.LIBRARY_WISHLIST),
        };
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listOptions);
        listSpinner.setAdapter(listAdapter);

        model.getGames().observe(this, (games) -> {
            gamesAdapter.setGames(games);
        });



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

//        model.getErrorMessage().observe(this, (errorMessage) -> {
//
//            errorText.setText(errorMessage);
//        });

        model.getSnackbarMessage().observe(this, (snackbarMessage) -> {
            if (snackbarMessage != null) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });



        //Register Listeners
        consoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<String> option = (SpinnerOption<String>) parent.getItemAtPosition(position) ;
                model.filterGamesByConsole(option.getValue());
                Log.i(LOG_TAG, "Filter by Console: " + option.getValue());
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//do Nothing my guy
            }
        });
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<GameList> option = (SpinnerOption<GameList>) parent.getItemAtPosition(position);
                model.filterGamesByList(option.getValue());
                Log.i(LOG_TAG, "Filter by list: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

    }
// Out of On create

    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_game_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as temporal navigation
            onBackPressed();
            return true;
        } else if(itemId == R.id.actionSignOut){
            onSignOut();
            return true;
        }
        else if(itemId == R.id.actionGetProfile){
            onGetProfile();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onBackPressed(){
        Log.i(LOG_TAG, "back pressed.");
    }
    public void onSignOut(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener((result)->{
            Log.i(LOG_TAG, "Signed out.");
            finish();
        });
    }
    public void onGetProfile(){
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

    }
