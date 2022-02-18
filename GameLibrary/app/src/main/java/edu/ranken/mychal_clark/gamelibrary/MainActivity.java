package edu.ranken.mychal_clark.gamelibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.data.Game;
import edu.ranken.mychal_clark.gamelibrary.ui.GameListAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.GameListModel;
import edu.ranken.mychal_clark.gamelibrary.ui.GameViewHolder;

public class MainActivity extends AppCompatActivity {



//views
    private RecyclerView recyclerView;

    //states
    private GameListAdapter adapter;
    private GameListModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //find views
        recyclerView = findViewById(R.id.gameList);

        //create adapter
        adapter = new GameListAdapter(this, null);


        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


       // Test if works
//        ArrayList<Game> newGame =  new ArrayList<Game>();
//        newGame.add(new Game("name","something", 2018));
//        newGame.add(new Game("war","some",2014));
//adapter.setItems(newGame);

//bind Model
        model = new ViewModelProvider(this).get(GameListModel.class);
        model.getGames().observe(this, (games) -> {
            adapter.setItems(games);
        });




    }
}