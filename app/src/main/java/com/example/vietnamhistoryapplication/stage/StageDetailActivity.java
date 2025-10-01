package com.example.vietnamhistoryapplication.stage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.example.vietnamhistoryapplication.R;

public class StageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage_detail_activity);

        // Lấy dữ liệu từ Intent
        String stageSlug = getIntent().getStringExtra("stageSlug");
        String title = getIntent().getStringExtra("title");
        String range = getIntent().getStringExtra("range");
        String overview = getIntent().getStringExtra("overview");

        // Gán dữ liệu vào các TextView
        TextView tvStageTitle = findViewById(R.id.tvStageTitle);
        TextView tvStageRange = findViewById(R.id.tvStageRange);
        TextView tvStageOverview = findViewById(R.id.tvStageOverview);

        tvStageTitle.setText(title != null ? title : "Không có tiêu đề");
        tvStageRange.setText(range != null ? range : "Không có thời gian");
        tvStageOverview.setText(overview != null ? overview : "Không có mô tả");
    }
}