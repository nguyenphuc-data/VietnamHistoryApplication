package com.example.vietnamhistoryapplication.home;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.home.PersonFragment.PersonPeriodFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.vietnamhistoryapplication.home.PeriodFragment.PeriodFragment;
import com.example.vietnamhistoryapplication.home.GameFragment.GameFragment;
import com.example.vietnamhistoryapplication.home.ExploreFragment.ExploreFragment;
import com.example.vietnamhistoryapplication.home.ProfileFragment.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_activity);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item ->{
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if(itemId ==R.id.nav_period){
                selectedFragment = new PeriodFragment();
            }else if(itemId == R.id.nav_character){
                selectedFragment = new PersonPeriodFragment();
            }else if(itemId == R.id.nav_game){
                selectedFragment = new GameFragment();
            }else if(itemId == R.id.nav_explore){
                selectedFragment = new ExploreFragment();
            }else if(itemId ==R.id.nav_profile){
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
            }
            return true;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PeriodFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_period);
        }
    }
}