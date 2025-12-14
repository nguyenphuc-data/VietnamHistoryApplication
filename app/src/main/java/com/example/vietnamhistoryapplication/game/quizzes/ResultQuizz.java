package com.example.vietnamhistoryapplication.game.quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.QuestionItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ResultQuizz extends AppCompatActivity {

    private String title;
    private int score;
    private int totalTime;
    private int questionCount;
    private List<QuestionItem> questions;
    private List<Integer> answerResults;
    private TextView tvQuizTitle, tvScore, tvscoreQuest, tvTotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.result_quizz_activity);

        title = getIntent().getStringExtra("title");
        score = getIntent().getIntExtra("score", 0);
        totalTime = getIntent().getIntExtra("totalTime", 0);
        questionCount = getIntent().getIntExtra("questionCount", 0);
        questions = (ArrayList<QuestionItem>) getIntent().getSerializableExtra("questions");
        answerResults = (ArrayList<Integer>) getIntent().getSerializableExtra("answerResults");

        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvScore = findViewById(R.id.tvScore);
        tvscoreQuest = findViewById(R.id.tvscoreQuest);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        FloatingActionButton btnBack = findViewById(R.id.btnBack);


        tvQuizTitle.setText(title != null ? title : "Kết quả Quiz");
        tvScore.setText(score*10 + " điểm");
        tvscoreQuest.setText("Số câu đúng: " + score + "/" + questionCount);
        tvTotalTime.setText("Tổng thời gian: " + totalTime + "s");


        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Hiển thị chi tiết đáp án
        RecyclerView rvAnswers = findViewById(R.id.rv_answers);
        rvAnswers.setLayoutManager(new LinearLayoutManager(this));
        AnswerDetailAdapter adapter = new AnswerDetailAdapter(questions, answerResults);
        rvAnswers.setAdapter(adapter);
    }
}
