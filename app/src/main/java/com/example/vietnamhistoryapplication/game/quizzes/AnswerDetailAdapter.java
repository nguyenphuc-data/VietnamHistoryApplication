package com.example.vietnamhistoryapplication.game.quizzes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.QuestionItem;

import java.util.List;

public class AnswerDetailAdapter extends RecyclerView.Adapter<AnswerDetailAdapter.ViewHolder> {

    private List<QuestionItem> questions;
    private List<Integer> answerResults;

    public AnswerDetailAdapter(List<QuestionItem> questions, List<Integer> answerResults) {
        this.questions = questions;
        this.answerResults = answerResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionItem q = questions.get(position);
        int userChoice = answerResults.get(position); // -1 hoặc 0-3
        int correctIndex = q.getCorrectAnswer();

        holder.tvQuestionNumber.setText("Câu " + (position + 1) + ":");
        holder.tvQuestion.setText(q.getQuestion());

        List<String> options = q.getOptions();
        holder.tvOptionA.setText("A. " + options.get(0));
        holder.tvOptionB.setText("B. " + options.get(1));
        holder.tvOptionC.setText("C. " + options.get(2));
        holder.tvOptionD.setText("D. " + options.get(3));

        // Reset màu
        int defaultColor = Color.parseColor("#000000");
        holder.tvOptionA.setTextColor(defaultColor);
        holder.tvOptionB.setTextColor(defaultColor);
        holder.tvOptionC.setTextColor(defaultColor);
        holder.tvOptionD.setTextColor(defaultColor);

        // Đáp án đúng luôn xanh
        int green = Color.parseColor("#4CAF50");
        switch (correctIndex) {
            case 0: holder.tvOptionA.setTextColor(green); break;
            case 1: holder.tvOptionB.setTextColor(green); break;
            case 2: holder.tvOptionC.setTextColor(green); break;
            case 3: holder.tvOptionD.setTextColor(green); break;
        }

        // Nếu người chơi chọn sai → tô đỏ đáp án họ chọn
        int red = Color.parseColor("#F44336");
        if (userChoice != -1 && userChoice != correctIndex) {
            switch (userChoice) {
                case 0: holder.tvOptionA.setTextColor(red); break;
                case 1: holder.tvOptionB.setTextColor(red); break;
                case 2: holder.tvOptionC.setTextColor(red); break;
                case 3: holder.tvOptionD.setTextColor(red); break;
            }
        }

        // Nếu chưa chọn (-1) → ghi chú
        if (userChoice == -1) {
            holder.tvQuestion.append(" (Chưa chọn)");
            holder.tvQuestion.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestion;
        TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
            tvQuestion = itemView.findViewById(R.id.tv_question);
            tvOptionA = itemView.findViewById(R.id.tv_option_a);
            tvOptionB = itemView.findViewById(R.id.tv_option_b);
            tvOptionC = itemView.findViewById(R.id.tv_option_c);
            tvOptionD = itemView.findViewById(R.id.tv_option_d);
        }
    }
}