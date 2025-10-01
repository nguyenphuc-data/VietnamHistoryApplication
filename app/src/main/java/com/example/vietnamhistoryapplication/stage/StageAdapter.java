package com.example.vietnamhistoryapplication.stage;

import android.content.Context;
import android.content.Intent;
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

public class StageAdapter extends RecyclerView.Adapter<StageAdapter.ViewHolder> {
    private List<StageItem> stageList;
    private Context context; // Thêm Context để sử dụng Intent

    public StageAdapter(List<StageItem> stageList) {
        this.stageList = stageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // Khởi tạo Context từ parent
        View view = LayoutInflater.from(context)
                .inflate(R.layout.stages_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StageItem stage = stageList.get(position);
        holder.tvTitle.setText(stage.title != null ? stage.title : "No Title");
        holder.tvPeriod.setText(stage.stageRange != null ? stage.stageRange : "No Period");
        holder.tvDescription.setText(stage.overview != null ? stage.overview : "No Overview");

        // Chỉnh sửa phần tải ảnh
        if (stage.image != null) {
            ImageLoader.loadImage(holder.ivImage, stage.image); // Sử dụng stage.image thay vì period.image
        } else {
            holder.ivImage.setImageResource(R.drawable.background_1); // Hình ảnh mặc định nếu không có
        }
        // Thêm sự kiện click để chuyển sang StageDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StageDetailActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPeriod, tvDescription;
        ImageView ivImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}