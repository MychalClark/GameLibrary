package edu.ranken.mychal_clark.gamelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ranken.mychal_clark.gamelibrary.ui.home.HomePageAdapter;

public class HomeActivity extends AppCompatActivity {

    private String LOG_TAG = "HomeActivity";
    //create views
    private ViewPager2 pager;
    private BottomNavigationView bottomNav;

    //states
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        //find views
        pager = findViewById(R.id.homePager);
        bottomNav = findViewById(R.id.homeBottomNav);

        //create adapter
        adapter = new HomePageAdapter(this);
        pager.setAdapter(adapter);

        //register listener
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });
        bottomNav.setOnItemSelectedListener((MenuItem item) -> {
            int itemId = item.getItemId();
            if (itemId == R.id.actionGameList) {
                pager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.actionUserList) {
                pager.setCurrentItem(1);
                return true;
            } else {
                return false;
            }
        });




    }


    @Override public boolean onCreateOptionsMenu(Menu menu){
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

        ConfirmDialog confirmDialog = new ConfirmDialog(
            HomeActivity.this,
            "Are you sure you want to log out?",
            (which) -> AuthUI.getInstance().signOut(this).addOnCompleteListener((result)->{
                Log.i(LOG_TAG, "Signed out.");
                finish();
            }),
            (which) -> {Toast.makeText(HomeActivity.this, "cancel", Toast.LENGTH_SHORT).show();});
        confirmDialog.show();


    }
    public void onGetProfile(){
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

}
