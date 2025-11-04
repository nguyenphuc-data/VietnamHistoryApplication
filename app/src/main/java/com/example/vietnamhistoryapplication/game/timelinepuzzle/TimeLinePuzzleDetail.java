package com.example.vietnamhistoryapplication.game.timelinepuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.models.Era;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimeLinePuzzleDetail extends AppCompatActivity {

    private TextView tvEraName, tvShortDesc, tvGameRules;
    private MaterialButton  btnStartGame;
    private FloatingActionButton btnBack;

    private Era era;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_puzzle_detail);

        tvEraName = findViewById(R.id.tvEraName);
        tvShortDesc = findViewById(R.id.tvShortDesc);
        tvGameRules = findViewById(R.id.tvGameRules);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnBack = findViewById(R.id.btnBack);

        era = (Era) getIntent().getSerializableExtra("era");
        if (era == null) {
            Toast.makeText(this, "Lỗi: Không có dữ liệu thời kỳ", Toast.LENGTH_SHORT).show();
            finish();
            return ;
        }
        tvEraName.setText(era.getName());
        tvShortDesc.setText(era.getShortDesc());

        btnBack.setOnClickListener(v -> finish());

        btnStartGame.setOnClickListener(v -> {
//            Intent intent = new Intent(this, TimeLineGameActivity.class);
//            intent.putExtra(TimeLineGameActivity.EXTRA_ERA, era); // Truyền tiếp
//            startActivity(intent);
        });

    }
}