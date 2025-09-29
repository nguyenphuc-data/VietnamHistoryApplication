package com.example.vietnamhistoryapplication.period;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PeriodDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.period_detail_activity);

        String slug = getIntent().getStringExtra("period_slug");
        Log.d("PeriodDetailActivity", "Created with slug: " + slug);

        // Xử lý WindowInsets để tránh bị che bởi system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Đặt trước trạng thái mặc định nhưng KHÔNG trigger listener
        bottomNav.setSelectedItemId(R.id.nav_period);

        // Listener chuyển màn khi người dùng chọn tab khác
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_period) {
                // Đang ở PeriodDetailActivity, không làm gì
                return true;
            }

            Intent intent = new Intent(this, HomeActivity.class);
            if (itemId == R.id.nav_character) {
                intent.putExtra("fragment", "character");
            } else if (itemId == R.id.nav_game) {
                intent.putExtra("fragment", "game");
            } else if (itemId == R.id.nav_explore) {
                intent.putExtra("fragment", "explore");
            } else if (itemId == R.id.nav_profile) {
                intent.putExtra("fragment", "profile");
            }

            startActivity(intent);
            finish();
            return true;
        });
    }
}
