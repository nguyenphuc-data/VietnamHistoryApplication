package com.example.vietnamhistoryapplication.game.timelinepuzzle;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.Event;
import java.util.List;

public class TimelineSlotAdapter extends RecyclerView.Adapter<TimelineSlotAdapter.SlotViewHolder> {

    private List<Event> placedEvents;

    public TimelineSlotAdapter(List<Event> placedEvents) {
        this.placedEvents = placedEvents;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Event event = placedEvents.get(position);

        // --- KHẮC PHỤC: KIỂM TRA NULL VÀ ẨN/HIỆN GIAO DIỆN ---
        if (event != null) {
            // 1. Ô đã điền: Hiển thị dữ liệu
            holder.tvSlotName.setText(event.getName());
            holder.tvSlotYear.setText(String.valueOf(event.getYear()));
            holder.tvSlotDesc.setText(event.getDesc());
            // 2. Hiện tất cả TextView
            holder.itemView.setBackground(null);
            holder.tvSlotName.setVisibility(View.VISIBLE);
            holder.tvSlotYear.setVisibility(View.VISIBLE);
            holder.tvSlotDesc.setVisibility(View.VISIBLE);
            holder.ivCardImage.setVisibility(View.VISIBLE);
            holder.ivDivider.setVisibility(ViewGroup.VISIBLE);
            // 3. Đặt Background cho ô đã điền (Tùy chỉnh nếu cần)
            // Ví dụ: holder.itemView.setBackgroundResource(R.drawable.bg_timeline_filled);

        } else {
            // 1. Ô trống: Xóa tất cả nội dung

            // 2. ẨN TẤT CẢ TextView. Dùng INVISIBLE để giữ lại không gian ô
            holder.tvSlotName.setVisibility(View.INVISIBLE);
            holder.tvSlotYear.setVisibility(View.INVISIBLE);
            holder.tvSlotDesc.setVisibility(View.INVISIBLE);
            holder.ivCardImage.setVisibility(View.INVISIBLE);
            holder.ivDivider.setVisibility(View.INVISIBLE);
            // 3. Xóa Background để ô trở nên trong suốt
            GradientDrawable border = new GradientDrawable();

            border.setStroke(2, Color.parseColor("#FFC107"), 8, 8); // Độ dày 2dp, Màu trắng, gạch 8dp, khoảng cách 4dp
            border.setCornerRadius(8); // Bo góc

            holder.itemView.setBackground(border);
        }
    }

    @Override
    public int getItemCount() {
        return placedEvents.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotName;
        TextView tvSlotYear;
        TextView tvSlotDesc;
        ImageView ivCardImage;
        View ivDivider;



        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotName = itemView.findViewById(R.id.tvSlotName);
            tvSlotYear = itemView.findViewById(R.id.tvSlotYear);
            tvSlotDesc = itemView.findViewById(R.id.tvSlotDesc);
            ivCardImage = itemView.findViewById(R.id.ivCardImage);
            ivDivider = itemView.findViewById(R.id.divider);
        }
    }
}