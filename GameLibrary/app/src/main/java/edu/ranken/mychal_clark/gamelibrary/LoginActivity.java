package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.ui.user.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;

    private ActivityResultLauncher<Intent> signInLauncher;

    //FIXME: do not use the view model for another screen here
    //       create a special LoginViewModel for this screen only (fixed)
    private LoginViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // FIXME: follow standard formatting and indention rules(fixed i think)
        //register views
        loginBtn = findViewById(R.id.loginBtn);
        model = new LoginViewModel();

        //register callback
        signInLauncher =
            registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                (result) -> onSignInResult(result)
            );

        //register listeners
        loginBtn.setOnClickListener((view) -> {
            // Choose Authentication providers
            List<AuthUI.IdpConfig> providers = new ArrayList<>();
            providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
            providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());  // FIXME: Google login method not enabled in Firebase Console(fixed)
            // Create sign-in intent
            Intent signInIntent =
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();

            // Launch sign-in activity
            signInLauncher.launch(signInIntent);

        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            onLoginSuccess(user);
        } else {
            loginBtn.performClick();
        }
    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.i("LoginActivity", "sign-in successful: " + user.getUid());
            onLoginSuccess(user);


        } else {
            FirebaseUiException error = result.getIdpResponse().getError();
            Log.e("LoginActivity", "sign-in failed", error);
        }
    }

    private void onLoginSuccess(FirebaseUser user) {
        model.createUser(() -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }
}