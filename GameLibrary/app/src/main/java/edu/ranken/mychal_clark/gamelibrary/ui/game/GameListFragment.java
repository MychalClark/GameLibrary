package edu.ranken.mychal_clark.gamelibrary.ui.game;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.Consoles;
import edu.ranken.mychal_clark.gamelibrary.data.GameList;
import edu.ranken.mychal_clark.gamelibrary.ui.utils.SpinnerOption;

public class GameListFragment extends Fragment {

    private static final String LOG_TAG = GameListFragment.class.getSimpleName();


    //views
    private RecyclerView recyclerView;
    private Spinner consoleSpinner;
    private Spinner listSpinner;


    //states
    private GameListModel model;
    private GameListAdapter gamesAdapter;

    private ArrayAdapter<SpinnerOption<String>> consolesAdapter;
    private ArrayAdapter<SpinnerOption<GameList>> listAdapter;


    public GameListFragment() {
        super(R.layout.game_list);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
//find Views
        consoleSpinner = view.findViewById(R.id.consoleSpinner);
        listSpinner = view.findViewById(R.id.listSpinner);
        recyclerView = view.findViewById(R.id.gameList);

//get Activity
        FragmentActivity activity = getActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        // setup recycler view
        int columns = getResources().getInteger(R.integer.gameListColumns);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, columns));

        //recycler and adapter attach
        model = new ViewModelProvider(activity).get(GameListModel.class);
        gamesAdapter = new GameListAdapter(activity, model);
        recyclerView.setAdapter(gamesAdapter);

        if(savedInstanceState !=null){
            int consoleSelectedIndex = savedInstanceState.getInt("consoleSelectedIndex");
            int listSelectedIndex = savedInstanceState.getInt("listSelectedIndex");
            consoleSpinner.setSelection(consoleSelectedIndex);
            listSpinner.setSelection(listSelectedIndex);
        }

//bind Model


        //Populate Spinner
        SpinnerOption<GameList>[] listOptions = new SpinnerOption[]{
            new SpinnerOption<>(getString(R.string.allGames), GameList.ALL_GAMES),
            new SpinnerOption<>(getString(R.string.myLibrary), GameList.LIBRARY),
            new SpinnerOption<>(getString(R.string.myWishlist), GameList.WISHLIST),
        };
        listAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, listOptions);
        listSpinner.setAdapter(listAdapter);

        model.getGames().observe(lifecycleOwner, (games) -> {
            gamesAdapter.setGames(games);
        });
        model.getWishList().observe(lifecycleOwner, (wishlist) -> {
            gamesAdapter.setWishlist(wishlist);
        });
        model.getLibrary().observe(lifecycleOwner, (library) -> {
            gamesAdapter.setLibrary(library);
        });

        model.getSnackbarMessage().observe(getViewLifecycleOwner(), (messageId) -> {
            if (messageId != null) {
                Snackbar.make(consoleSpinner, messageId, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            } else {
                // no message to show
            }
        });



        model.getConsoles().observe(lifecycleOwner, (consoles) -> {
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

                consolesAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, consoleNames);
                consoleSpinner.setAdapter(consolesAdapter);
                consoleSpinner.setSelection(selectedPosition, false);
            }
        });

//        model.getSnackbarMessage().observe(lifecycleOwner, (snackbarMessage) -> {
//            if (snackbarMessage != null) {
//                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
//                model.clearSnackbar();
//            }
//        });


        //Register Listeners
        consoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption<String> option = (SpinnerOption<String>) parent.getItemAtPosition(position);
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
                gamesAdapter.setMode(option.getValue());
                Log.i(LOG_TAG, "Filter by list: " + option.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

    }
}