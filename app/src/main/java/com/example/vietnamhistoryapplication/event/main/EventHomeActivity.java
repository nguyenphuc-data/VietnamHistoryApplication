package com.example.vietnamhistoryapplication.event.main;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.event.eventDetail.EventDetailFragment;
import com.example.vietnamhistoryapplication.home.PeriodFragment.PeriodFragment;
import com.example.vietnamhistoryapplication.home.PersonFragment.PersonPeriodFragment;
import com.example.vietnamhistoryapplication.home.GameFragment.GameFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_home);  // Layout chứa BottomNavigationView
        String periodSlug = getIntent().getStringExtra("periodSlug");
        String stageSlug = getIntent().getStringExtra("stageSlug");
        String eventSlug = getIntent().getStringExtra("eventSlug");


        Log.d("EventHomeActivity", "periodSlug: " + periodSlug);
        Log.d("EventHomeActivity", "stageSlug: " + stageSlug);
        Log.d("EventHomeActivity", "eventSlug: " + eventSlug);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_event) {
                selectedFragment = new EventDetailFragment();
                Bundle args = new Bundle();
                args.putString("periodSlug", periodSlug);
                args.putString("stageSlug", stageSlug);
                args.putString("eventSlug", eventSlug);
                selectedFragment.setArguments(args);
            } else if (itemId == R.id.nav_character) {
                selectedFragment = new PersonPeriodFragment();
            } else if (itemId == R.id.nav_game) {
                selectedFragment = new GameFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

        // Nếu savedInstanceState là null, hiển thị EventDetailFragment mặc định
        if (savedInstanceState == null) {
            EventDetailFragment eventDetailFragment = new EventDetailFragment();
            // Truyền tham số vào EventDetailFragment
            Bundle args = new Bundle();
            args.putString("periodSlug", periodSlug);
            args.putString("stageSlug", stageSlug);
            args.putString("eventSlug", eventSlug);
            eventDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, eventDetailFragment)
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_event);  // Set default selected item
        }
    }
}
