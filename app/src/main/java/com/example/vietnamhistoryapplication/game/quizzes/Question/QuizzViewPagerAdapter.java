package com.example.vietnamhistoryapplication.game.quizzes.Question;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vietnamhistoryapplication.models.QuestionItem;

import java.util.List;

public class QuizzViewPagerAdapter extends FragmentStateAdapter {
    private List<QuestionItem> questions;
    private OnQuestionActionListener listener;
    private Long timeLimit;

    public QuizzViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,Long timeLimit, List<QuestionItem> questions, OnQuestionActionListener listener) {
        super(fragmentActivity);
        this.timeLimit = timeLimit;
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new QuestionFragment(questions.get(position),timeLimit,position, listener);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public interface OnQuestionActionListener {
        void onAnswerSubmitted(boolean isCorrect);
        void onNextClicked(int currentPosition);
    }
}