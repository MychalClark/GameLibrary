package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ranken.mychal_clark.gamelibrary.ui.game.GameDetailsFragment;
import edu.ranken.mychal_clark.gamelibrary.ui.game.GameDetailsViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.game.GameListModel;
import edu.ranken.mychal_clark.gamelibrary.ui.home.HomePageAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.utils.ConfirmDialog;
import edu.ranken.mychal_clark.gamelibrary.ui.userProfile.UserListViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.userProfile.UserProfileFragment;
import edu.ranken.mychal_clark.gamelibrary.ui.userProfile.UserProfileViewModel;

public class HomeActivity extends AppCompatActivity {

    private String LOG_TAG = HomeActivity.class.getSimpleName();
    //create views
    private ViewPager2 pager;
    private BottomNavigationView bottomNav;
    private FragmentContainerView detailsContainer;

    //states
    private HomePageAdapter adapter;
    private GameListModel gameListModel;
    private UserListViewModel userListViewModel;
    private GameDetailsViewModel gameDetailsViewModel;
    private UserProfileViewModel userProfileViewModel;
    private String gameId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        //find views
        pager = findViewById(R.id.homePager);
        bottomNav = findViewById(R.id.homeBottomNav);
        detailsContainer = findViewById(R.id.homeDetailsContainer);

        //create adapter
        adapter = new HomePageAdapter(this);
        pager.setAdapter(adapter);

        //register listener
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.getMenu().getItem(position).setChecked(true);
                if (position == 0) {
                    if (detailsContainer != null) {
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.homeDetailsContainer, GameDetailsFragment.class, null)
                            .commit();
                    }
                } else if (position == 1) {
                    if (detailsContainer != null) {
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.homeDetailsContainer, UserProfileFragment.class, null)
                            .commit();
                    }
                }
            }
        });
        bottomNav.setOnItemSelectedListener((MenuItem item) -> {
            int itemId = item.getItemId();
            if (itemId == R.id.actionGameList) {
                pager.setCurrentItem(0);
                if(detailsContainer != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeDetailsContainer, GameDetailsFragment.class, null)
                        .commit();
                }
                return true;
            } else if (itemId == R.id.actionUserList) {
                pager.setCurrentItem(1);
                if(detailsContainer !=null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeDetailsContainer, UserProfileFragment.class, null)
                        .commit();
                }
                return true;
            } else {
                return false;
            }
        });

        //get models
        gameListModel = new ViewModelProvider(this).get(GameListModel.class);
        userListViewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        gameDetailsViewModel = new ViewModelProvider(this).get(GameDetailsViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        //restore saved state
        if (savedInstanceState != null) {
            pager.setCurrentItem(savedInstanceState.getInt("page"));
            gameId = savedInstanceState.getString("gameId");
            userId = savedInstanceState.getString("userId");
        }
        //observe Models
        gameListModel.getSelectedGame().observe(this, (game) -> {
            gameId = game != null ? game.id : null;
            
            if (detailsContainer == null) {
                if (game != null) {
                    gameListModel.setSelectedGame(null);

                    Intent intent = new Intent(this, GameDetailsActivity.class);
                    intent.putExtra(GameDetailsActivity.EXTRA_GAME_ID, game.id);
                    this.startActivity(intent);
                }
            }else{
                if(game != null){
                    gameDetailsViewModel.fetchGame(game.id);
                }else{
                    gameDetailsViewModel.fetchGame(null);
                }
            }
        });

        userListViewModel.getSelectedUser().observe(this, (user) -> {
            userId = user != null ? user.id : null;
            if (detailsContainer == null) {
                if (user != null) {
                    userListViewModel.setSelectedUser(null);

                    Intent intent = new Intent(this, UserProfileActivity.class);
                    intent.putExtra(UserProfileActivity.EXTRA_USER_ID, user.id);
                    this.startActivity(intent);
                }
            }else{
                if(user != null){
                    userProfileViewModel.fetchUser(user.id);
                }else{
                    userProfileViewModel.fetchUser(null);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as temporal navigation
            onBackPressed();
            return true;
        } else if (itemId == R.id.actionSignOut) {
            onSignOut();
            return true;
        } else if (itemId == R.id.actionGetProfile) {
            onGetProfile();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putInt("page", pager.getCurrentItem());
        outState.putString("gameId", gameId);
        outState.putString("userId", userId);
    }

    @Override
    public void onBackPressed() {
        Log.i(LOG_TAG, "back pressed.");
    }

    public void onSignOut() {

        ConfirmDialog confirmDialog = new ConfirmDialog(
            HomeActivity.this,
            getString(R.string.confirmSignOut),
            (which) -> AuthUI.getInstance().signOut(this).addOnCompleteListener((result) -> {
                Log.i(LOG_TAG, "Signed out.");
                finish();
            }),
            (which) -> {
                Toast.makeText(HomeActivity.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            });
        confirmDialog.show();


    }

    public void onGetProfile() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

}
