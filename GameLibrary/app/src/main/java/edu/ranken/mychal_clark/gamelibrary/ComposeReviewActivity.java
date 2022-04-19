package edu.ranken.mychal_clark.gamelibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ui.review.ComposeReviewViewModel;

public class ComposeReviewActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameDetailsActivity.class.getSimpleName();
    public static final String EXTRA_GAME_ID = "gameId";

    private TextView gameTitle;
    private EditText reviewText;
    private String gameId;
    private ComposeReviewViewModel model;
    private Button reviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_review);

        reviewText = findViewById(R.id.reviewInput);
        gameTitle = findViewById(R.id.reviewGameTitle);
        reviewBtn = findViewById(R.id.reviewBtn);


        Intent intent = getIntent();
        gameId = intent.getStringExtra(GameDetailsActivity.EXTRA_GAME_ID);

        Log.i(LOG_TAG, "gameId here " + gameId);
        //bind Model here
        model = new ViewModelProvider(this).get(ComposeReviewViewModel.class);
        model.fetchGame(gameId);
        model.getGameName().observe(this,(gameName)->{
            gameTitle.setText(gameName);
            });


        reviewBtn.setOnClickListener((view) -> { model.publishReview(gameId, reviewText.getText().toString());
hideKeyboard(this,reviewText);
        });

        model.getFinished().observe(this,(finish)->{
            if(Objects.equals(finish,Boolean.TRUE)){
                finish();

            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as temporal navigation
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm =
            (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}