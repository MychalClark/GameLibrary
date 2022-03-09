package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.ui.MyProfileViewModel;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;

    private ActivityResultLauncher<Intent> signInLauncher;

    private MyProfileViewModel profileViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        profileViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);
        //regis views
        loginBtn = findViewById(R.id.loginBtn);


//rgis callback
        signInLauncher =
            registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                (result) -> onSignInResult(result)
            );


        //register listeners
        loginBtn.setOnClickListener((view) -> {
            
// Choose Authenication providers
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){



            onLoginSuccess(user);
        } else{loginBtn.performClick();}
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
       profileViewModel.createUser();
        Intent intent = new Intent(this, GameListActivity.class);
        startActivity(intent);
    }
}