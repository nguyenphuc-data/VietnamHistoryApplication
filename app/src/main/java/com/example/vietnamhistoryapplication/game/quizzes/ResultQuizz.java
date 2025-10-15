package com.example.vietnamhistoryapplication.game.quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietnamhistoryapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ResultQuizz extends AppCompatActivity {

    private String title;
    private int score;
    private int totalTime;
    private int questionCount;

    private TextView tvQuizTitle, tvScore, tvscoreQuest, tvTotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.result_quizz_activity);

        // --- Nhận dữ liệu từ Intent ---
        title = getIntent().getStringExtra("title");
        score = getIntent().getIntExtra("score", 0);
        totalTime = getIntent().getIntExtra("totalTime", 0);
        questionCount = getIntent().getIntExtra("questionCount", 0);

        // --- Ánh xạ view ---
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvScore = findViewById(R.id.tvScore);
        tvscoreQuest = findViewById(R.id.tvscoreQuest);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        FloatingActionButton btnBack = findViewById(R.id.btnBack);

        // --- Hiển thị dữ liệu ---
        tvQuizTitle.setText(title != null ? title : "Kết quả Quiz");
        tvScore.setText(score*10 + " điểm");
        tvscoreQuest.setText("Số câu đúng: " + score + "/" + questionCount);
        tvTotalTime.setText("Tổng thời gian: " + totalTime + "s");

        // --- Nút quay lại ---
        btnBack.setOnClickListener(v -> {
            finish(); // Quay về activity trước (QuizzPlay)
        });
    }
}
