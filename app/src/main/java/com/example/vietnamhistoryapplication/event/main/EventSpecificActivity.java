package com.example.vietnamhistoryapplication.event.main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.event.eventDetail.EventDetailFragment;

public class EventSpecificActivity extends AppCompatActivity {
    private String periodSlug, stageSlug, eventSlug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_specific_activity);
        String periodSlug = getIntent().getStringExtra("periodSlug");
        String stageSlug = getIntent().getStringExtra("stageSlug");
        String eventSlug = getIntent().getStringExtra("eventSlug");
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
    }
}