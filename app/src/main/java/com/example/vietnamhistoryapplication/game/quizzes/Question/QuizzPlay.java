package com.example.vietnamhistoryapplication.game.quizzes.Question;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.game.quizzes.ResultQuizz;
import com.example.vietnamhistoryapplication.models.QuestionItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizzPlay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewPager2 viewPager;
    private int totalTime= 0;
    private QuizzViewPagerAdapter adapter;

    private String title;
    private Long timeLimit;
    CountDownTimer totalTimer;
    private int score = 0;
    private List<QuestionItem> questions = new ArrayList<>();
    private List<Integer> answerResults = new ArrayList<>();
    private String gameId, quizzId;
    private int questionCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quizz_play_activity);

         totalTimer = new CountDownTimer(Long.MAX_VALUE, 1000) { // đếm "vô hạn"
            @Override
            public void onTick(long millisUntilFinished) {
                    totalTime++; // mỗi giây tăng 1
            }

            @Override
            public void onFinish() {
            }
        }.start();
        gameId = getIntent().getStringExtra("gameId");
        quizzId = getIntent().getStringExtra("quizzItem");
        title = getIntent().getStringExtra("title");
        timeLimit = getIntent().getLongExtra("timeLimit", 0);
        questionCount = getIntent().getIntExtra("questionCount", 0);
        loadQuestion();
        viewPager = findViewById(R.id.view_pager);
        TextView tvTitle = findViewById(R.id.tvQuizName);
        tvTitle.setText(title);
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
         adapter = new QuizzViewPagerAdapter(this,timeLimit, questions, new QuizzViewPagerAdapter.OnQuestionActionListener() {
            @Override
            public void onAnswerSubmitted(boolean isCorrect, int mychoice) {
                if (isCorrect) score+=1;
                answerResults.add(mychoice);
            }

            @Override
            public void onNextClicked(int currentPosition) {
                if (currentPosition < questions.size() - 1) {
                    viewPager.setCurrentItem(currentPosition + 1, true);
                } else {
                    if (totalTimer != null) totalTimer.cancel();
                    endQuiz();
                }
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // Vô hiệu swipe để kiểm soát flow
    }

    private void endQuiz() {

        Intent intent = new Intent(this, ResultQuizz.class); // Giả sử có ResultActivity
        intent.putExtra("score", score);
        intent.putExtra("questionCount", questionCount);
        intent.putExtra("title",title);
        intent.putExtra("totalTime", totalTime);
        intent.putExtra("questions", new ArrayList<>(questions));
        intent.putExtra("answerResults", new ArrayList<>(answerResults));
        startActivity(intent);
        finish();
    }

}