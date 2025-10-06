package com.example.vietnamhistoryapplication.home.PersonFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;

import java.util.List;

public class PersonPeriodAdapter extends RecyclerView.Adapter<PersonPeriodAdapter.ViewHolder> {
    private List<PersonPeriodItem> personPeriodList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String slug);
    }

    public PersonPeriodAdapter(List<PersonPeriodItem> personPeriodList, OnItemClickListener listener) {
        this.personPeriodList = personPeriodList;
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPeriod;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.person_period_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonPeriodItem personPeriod = personPeriodList.get(position);
        holder.tvTitle.setText(personPeriod.title != null ? personPeriod.title : "No Title");
        holder.tvPeriod.setText(personPeriod.periodRange != null ? personPeriod.periodRange : "No Period");

        // Thêm phần tải ảnh
        if (personPeriod.image != null) {
            ImageLoader.loadImage(holder.ivImage, personPeriod.image);
        } else {
            holder.ivImage.setImageResource(R.drawable.background_1);
        }

        // Thêm sự kiện click để chuyển sang PersonListActivity với slug
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(personPeriod.slug);
            }
        });
    }

    @Override
    public int getItemCount() {
        return personPeriodList.size();
    }
}