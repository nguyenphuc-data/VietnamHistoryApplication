package com.example.vietnamhistoryapplication.game.quizzes.Question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private TextView tvResult, tvCorrectAnswer, tvExplanation;
    private Button btnSubmit, btnNext;
    private boolean isSubmitted = false;

    public QuestionFragment(QuestionItem question, QuizzViewPagerAdapter.OnQuestionActionListener listener) {
        this.question = question;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        // Khởi tạo các thành phần
        TextView tvQuestion = view.findViewById(R.id.tv_question);
        tvResult = view.findViewById(R.id.tv_result);
        tvCorrectAnswer = view.findViewById(R.id.tv_correct_answer);
        tvExplanation = view.findViewById(R.id.tv_explanation);

        btnNext = view.findViewById(R.id.btn_next);
        MaterialButtonToggleGroup quizGroup = view.findViewById(R.id.quiz_options_group);
        MaterialButton btn_option_a = view.findViewById(R.id.btn_option_a);
        MaterialButton btn_option_b = view.findViewById(R.id.btn_option_b);
        MaterialButton btn_option_c = view.findViewById(R.id.btn_option_c);
        MaterialButton btn_option_d = view.findViewById(R.id.btn_option_d);

        btn_option_a.setText(question.getOptions().get(0));
        btn_option_b.setText(question.getOptions().get(1));
        btn_option_c.setText(question.getOptions().get(2));
        btn_option_d.setText(question.getOptions().get(3));

        // Đặt câu hỏi
        tvQuestion.setText(question.getQuestion());

        // Thêm RadioButton động


        // Xử lý Submit


        return view;
    }


}