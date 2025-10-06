package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;

import java.util.List;

public class PersonDetailAchievementAdapter extends RecyclerView.Adapter<PersonDetailAchievementAdapter.ViewHolder> {
    private List<String> achievements;

    public PersonDetailAchievementAdapter(List<String> achievements) {
        this.achievements = achievements;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBulletItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBulletItem = itemView.findViewById(R.id.tvBulletItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_detail_items, parent, false); // Sử dụng item_bullet_text.xml, nhưng theo tree là person_detail_items.xml
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = achievements.get(position);
        holder.tvBulletItem.setText("• " + item);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }
}