package com.example.vietnamhistoryapplication.game.quizzes.Question;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.QuestionItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizzPlay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewPager2 viewPager;
    private Chronometer chronometer;
    private QuizzViewPagerAdapter adapter;

    private int score = 0;
    private List<QuestionItem> questions = new ArrayList<>();
    private String gameId, quizzId;
    private int questionCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quizz_play_activity);
        gameId = getIntent().getStringExtra("gameId");
        quizzId = getIntent().getStringExtra("quizzItem");
        questionCount = getIntent().getIntExtra("questionCount", 0);
        loadQuestion();
        viewPager = findViewById(R.id.view_pager);
        chronometer = findViewById(R.id.chronometer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());


        setupViewPager();

    }
    private void loadQuestion() {
        db.collection("games")
                .document(gameId)
                .collection("quizzes")
                .document(quizzId)
                .collection("questions")
                .orderBy("orderQuestion")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                String question = documentSnapshot.getString("question");
                                List<String> options = (List<String>)documentSnapshot.get("options");
                                int correctAnswer = documentSnapshot.getLong("correctAnswer").intValue();
                                int orderQuestion = documentSnapshot.getLong("orderQuestion").intValue();
                                String explanation = documentSnapshot.getString("explanation");
                                questions.add(new QuestionItem(question, options, correctAnswer, orderQuestion, explanation));
                            Log.d("QuizzPlay", "Loaded question:"+question);

                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }
    private void setupViewPager() {
         adapter = new QuizzViewPagerAdapter(this, questions, new QuizzViewPagerAdapter.OnQuestionActionListener() {
            @Override
            public void onAnswerSubmitted(boolean isCorrect) {
                if (isCorrect) score+=10;
                // Cập nhật giao diện nếu cần (tùy chọn)
            }

            @Override
            public void onNextClicked(int currentPosition) {
                if (currentPosition < questions.size() - 1) {
                    viewPager.setCurrentItem(currentPosition + 1, true);
                } else {
//                    endQuiz();
                }
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // Vô hiệu swipe để kiểm soát flow
    }

//    private void endQuiz() {
//        // Chuyển sang ResultActivity (cần tạo)
//        Intent intent = new Intent(this, ResultActivity.class); // Giả sử có ResultActivity
//        intent.putExtra("score", score);
//        intent.putExtra("totalTime", (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000); // Thời gian tạm
//        startActivity(intent);
//        finish();
//    }
}