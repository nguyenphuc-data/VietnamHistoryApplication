package com.example.vietnamhistoryapplication.home.GameFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.QuizzItem;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<QuizzItem> quizzList;

    public interface OnQuizClickListener {
        void onQuizClick(QuizzItem quizzItem);
    }
    OnQuizClickListener clickListener;

    public GameAdapter(List<QuizzItem> quizzList, OnQuizClickListener clickListener) {
        this.quizzList = quizzList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.games_items, parent, false);
        return new GameAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapter.ViewHolder holder, int position) {
        QuizzItem quizz = quizzList.get(position);
        // Add binding here
        holder.tvTitle.setText(quizz.getTitle());  // Adjust getters as needed
        holder.tvType.setText(quizz.getType());
        holder.tvLevel.setText(quizz.getLevel());

        holder.itemView.setOnClickListener(v -> clickListener.onQuizClick(quizz));
    }

    @Override
    public int getItemCount() {
        return quizzList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvType, tvLevel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvLevel = itemView.findViewById(R.id.tvLevel);
        }
    }
}