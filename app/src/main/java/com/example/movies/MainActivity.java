package com.example.movies;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;

import com.example.movies.config.ApplicationConfig;
import com.example.movies.fragments.HomeFragment;
import com.example.movies.fragments.PopularFragment;
import com.example.movies.fragments.SettingsFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    SearchView searchview;

    HomeFragment homeFragment = new HomeFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    PopularFragment popularFragment = new PopularFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView  = findViewById(R.id.bottom_navigation);
        searchview = findViewById(R.id.searchView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        /*BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);*/

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.popular:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,popularFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,settingsFragment).commit();
                        return true;
                    case R.id.logout:
                        displayClosingAlertBox(true);
                        return true;
                }

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Getting settings preference
        SharedPreferences settingsPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean allowNotification = settingsPreference.getBoolean("notifications", false);
        switch (item.getItemId()) {
            case R.id.notification:
                if(allowNotification) {
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                }
                else {
                    Toast.makeText(this, ApplicationConfig.NOTIFICATION_DISABLED_MESSAGE, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); Removed this line cause i didn't want to use the default action of back button
        displayClosingAlertBox(false);
    }

    private void displayClosingAlertBox(boolean isLogoutAction){
        String msg = "Are you sure you want to exit?";
        if(isLogoutAction) msg = msg.replace("exit", "logout");
        new AlertDialog.Builder(MainActivity.this)
                //.setIcon(android.R.drawable.star_on)
                .setTitle("Exiting the Application")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "ON STOP: YES");
                        if (isLogoutAction == true) {
                            SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                            preferences.edit().clear().commit();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        } else {
                            finishAffinity();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}