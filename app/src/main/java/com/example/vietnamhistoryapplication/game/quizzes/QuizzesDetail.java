package com.example.vietnamhistoryapplication.game.quizzes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.QuizzItem;

public class QuizzesDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout._quizzes_detailactivity);

        QuizzItem quizzItem = getIntent().getParcelableExtra("quizzItem");

    }
}