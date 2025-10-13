package com.example.vietnamhistoryapplication.game.quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.event.main.EventSpecificActivity;
import com.example.vietnamhistoryapplication.game.quizzes.Question.QuizzPlay;
import com.example.vietnamhistoryapplication.models.QuizzItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class QuizzesDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quizzes_detail_activity);

        QuizzItem quizzItem = (QuizzItem) getIntent().getSerializableExtra("quizzItem");
        FloatingActionButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnPlay = findViewById(R.id.btnPlay);
        MaterialButton btnShare = findViewById(R.id.btnShare);
        TextView tvQuizName = findViewById(R.id.tvQuizName);
        TextView tvQuizdes = findViewById(R.id.tvQuizdes);
        Log.d("QuizzesDetail", "Question count :"+ quizzItem.getQuestionCount());
        btnBack.setOnClickListener(v->{
            finish();
        });
        btnShare.setOnClickListener(v->{
            Intent intent = new Intent(this, EventSpecificActivity.class);
            intent.putExtra("periodSlug",quizzItem.getPeriodId());
            intent.putExtra("stageSlug",quizzItem.getstageId());
            intent.putExtra("eventSlug",quizzItem.getEventid());
            startActivity(intent);
            Log.d("QuizzesDetail","EventID: "+quizzItem.getEventId()+"");

        });
        btnPlay.setOnClickListener(v->{
            // Chuyển đến màn hình chơi quiz
            Intent intent = new Intent(this, QuizzPlay.class);
            intent.putExtra("gameId","quiz-lich-su-viet-nam");
            intent.putExtra("quizzItem",quizzItem.getQuizzslug());
            intent.putExtra("questionCount",quizzItem.getQuestionCount());
            intent.putExtra("title",quizzItem.getTitle());
            intent.putExtra("timeLimit",quizzItem.getTimeLimit());
            startActivity(intent);
        });
        tvQuizName.setText(quizzItem.getTitle());
        tvQuizdes.setText(quizzItem.getDescription());

    }
}