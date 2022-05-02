package edu.ranken.mychal_clark.checkmyreceipt;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private String LOG_TAG = LoginActivity.class.getSimpleName();

    private Button loginBtn;
    private ActivityResultLauncher<Intent> signInLauncher;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //find views
        loginBtn = findViewById(R.id.loginBtn);
        db = FirebaseFirestore.getInstance();

//register callback
        signInLauncher =
            registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                (result) -> onSignInResult(result)
            );




        //
        //register listeners
        loginBtn.setOnClickListener((view) -> {
            // Choose Authentication providers
            List<AuthUI.IdpConfig> providers = new ArrayList<>();
            providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
            providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());
            // Create sign-in intent
            Intent signInIntent =
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();

            // Launch sign-in activity
            signInLauncher.launch(signInIntent);

        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // FIXME: move to onCreate()
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();;
//        if(currentUser != null){
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            Log.i(LOG_TAG, "sign-in successful: " + user.getUid());
//
//            Intent intent = new Intent(this, ReceiptListActivity.class);
//            startActivity(intent);
//        }else {
//            loginBtn.performClick();
//        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.i(LOG_TAG, "sign-in successful: " + user.getUid());

            Intent intent = new Intent(this, ReceiptListActivity.class);
            startActivity(intent);

        } else {
           FirebaseUiException error = result.getIdpResponse().getError();
            Log.e(LOG_TAG, "sign-in failed", error);
        }
    }
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