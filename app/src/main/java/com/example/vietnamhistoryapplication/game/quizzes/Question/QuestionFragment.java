package com.example.vietnamhistoryapplication.game.quizzes.Question;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.QuestionItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class QuestionFragment extends Fragment {
    private QuestionItem question;
    private QuizzViewPagerAdapter.OnQuestionActionListener listener;
    private RadioGroup radioGroup;
    private boolean timerStarted = false;
    private TextView tvResult, tvCorrectAnswer, tvExplanation;
    private Button  btnNext;
    private CountDownTimer countDownTimer;
    private View view;
    private boolean isSubmitted = false;
    private Long timeLimit;
    private TextView tvTimer;
    private TextView tvQuestion;
    private TextView tvQuestionOrder;
    int position;

    public QuestionFragment(QuestionItem question, Long timeLimit,int position,QuizzViewPagerAdapter.OnQuestionActionListener listener) {
        this.question = question;
        this.listener = listener;
        this.position = position;
        this.timeLimit = timeLimit*1000;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_question, container, false);

        // Khởi tạo các thành phần
        TextView tvQuestion = view.findViewById(R.id.tv_question);
        tvResult = view.findViewById(R.id.tv_result);
        tvCorrectAnswer = view.findViewById(R.id.tv_correct_answer);
        tvExplanation = view.findViewById(R.id.tv_explanation);
        tvTimer = view.findViewById(R.id.tv_timer);
        btnNext = view.findViewById(R.id.btn_next);
        tvQuestionOrder = view.findViewById(R.id.tv_question_order);
        MaterialButtonToggleGroup quizGroup = view.findViewById(R.id.quiz_options_group);
        MaterialButton btn_option_a = view.findViewById(R.id.btn_option_a);
        MaterialButton btn_option_b = view.findViewById(R.id.btn_option_b);
        MaterialButton btn_option_c = view.findViewById(R.id.btn_option_c);
        MaterialButton btn_option_d = view.findViewById(R.id.btn_option_d);



//        Gán câu hỏi và câu trả lời
        tvQuestionOrder.setText("Câu "+(position+1));
        tvQuestion.setText(question.getQuestion());
        btn_option_a.setText(question.getOptions().get(0));
        btn_option_b.setText(question.getOptions().get(1));
        btn_option_c.setText(question.getOptions().get(2));
        btn_option_d.setText(question.getOptions().get(3));


        // Xử lí quizzGroup
        quizGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked ) {

                countDownTimer.cancel(); // Dừng đếm ngược

                int selectedIndex = -1;
                MaterialButton selectedButton = null;

                // Xác định nút được chọn
                if (checkedId == R.id.btn_option_a) {
                    selectedIndex = 0;
                    selectedButton = btn_option_a;
                } else if (checkedId == R.id.btn_option_b) {
                    selectedIndex = 1;
                    selectedButton = btn_option_b;
                } else if (checkedId == R.id.btn_option_c) {
                    selectedIndex = 2;
                    selectedButton = btn_option_c;
                } else if (checkedId == R.id.btn_option_d) {
                    selectedIndex = 3;
                    selectedButton = btn_option_d;
                }



                // Disable các nút sau khi chọn
                btn_option_a.setEnabled(false);
                btn_option_b.setEnabled(false);
                btn_option_c.setEnabled(false);
                btn_option_d.setEnabled(false);

                // Xác định đáp án đúng
                int correctIndex = question.getCorrectAnswer();
                MaterialButton correctButton = null;

                switch (correctIndex) {
                    case 0:
                        correctButton = btn_option_a;
                        break;
                    case 1:
                        correctButton = btn_option_b;
                        break;
                    case 2:
                        correctButton = btn_option_c;
                        break;
                    case 3:
                        correctButton = btn_option_d;
                        break;
                }
                showAnswerResult(view, selectedIndex, selectedButton,correctButton,correctIndex);


            }
        });


        // Xử lý Submit
        btnNext.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNextClicked(position);
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if ( !timerStarted) {
            timerStarted = true;
            startTimer(view);
        }
    }
    private void showAnswerResult(View view, int selectedIndex, MaterialButton selectedButton, MaterialButton correctButton, int correctIndex) {
        LinearLayout layoutResult = view.findViewById(R.id.layout_result);



        layoutResult.setVisibility(View.VISIBLE);
        layoutResult.setAlpha(0f);
        layoutResult.animate().alpha(1f).setDuration(400).start();

        if (selectedIndex == correctIndex) {
            tvResult.setText("🎉 Đúng rồi!");
            tvResult.setTextColor(Color.parseColor("#4CAF50"));
            selectedButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            );
            selectedButton.setStrokeColor(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            );
            selectedButton.setTextColor(Color.WHITE);
            listener.onAnswerSubmitted(true, correctIndex);
        } else {
            tvResult.setText("❌ Sai rồi!");
            tvResult.setTextColor(Color.parseColor("#E8582B"));
            selectedButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#E8582B"))
            );
            selectedButton.setStrokeColor(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#E8582B"))
            );
            selectedButton.setTextColor(Color.WHITE);
            correctButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            );
            correctButton.setStrokeColor(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            );
            correctButton.setTextColor(Color.WHITE);
            listener.onAnswerSubmitted(false, selectedIndex);
        }

        String correctText = question.getOptions().get(correctIndex);
        tvCorrectAnswer.setText(correctText);
        tvExplanation.setText(question.getExplanation());
        btnNext.setVisibility(View.VISIBLE);
    }
    private void showTimeoutResult(View view) {
        LinearLayout layoutResult = view.findViewById(R.id.layout_result);

        MaterialButton btnA = view.findViewById(R.id.btn_option_a);
        MaterialButton btnB = view.findViewById(R.id.btn_option_b);
        MaterialButton btnC = view.findViewById(R.id.btn_option_c);
        MaterialButton btnD = view.findViewById(R.id.btn_option_d);

        btnA.setEnabled(false);
        btnB.setEnabled(false);
        btnC.setEnabled(false);
        btnD.setEnabled(false);

        int correctIndex = question.getCorrectAnswer();
        MaterialButton correctButton = null;
        switch (correctIndex) {
            case 0: correctButton = btnA; break;
            case 1: correctButton = btnB; break;
            case 2: correctButton = btnC; break;
            case 3: correctButton = btnD; break;
        }

        correctButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
        );
        correctButton.setStrokeColor(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
        );
        correctButton.setTextColor(Color.WHITE);

        layoutResult.setVisibility(View.VISIBLE);
        layoutResult.setAlpha(0f);
        layoutResult.animate().alpha(1f).setDuration(400).start();

        tvResult.setText("⏰ Hết thời gian!");
        tvResult.setTextColor(Color.parseColor("#E8582B"));
        tvCorrectAnswer.setText(question.getOptions().get(correctIndex));
        tvExplanation.setText(question.getExplanation());
        btnNext.setVisibility(View.VISIBLE);

        if (listener != null) listener.onAnswerSubmitted(false, -1);
    }
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }
    private void startTimer(View view) {
        countDownTimer = new CountDownTimer(timeLimit, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvTimer.setText("⏳ " + secondsLeft + "s");
            }

            @Override
            public void onFinish() {
                    showTimeoutResult(view);
            }
        }.start();
    }

}