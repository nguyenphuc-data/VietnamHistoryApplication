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

        if (event != null) {
            holder.tvSlotName.setText(event.getName());
            holder.tvSlotYear.setText(String.valueOf(event.getYear()));
            holder.tvSlotDesc.setText(event.getDesc());
            holder.itemView.setBackground(null);
            holder.tvSlotName.setVisibility(View.VISIBLE);
            holder.tvSlotYear.setVisibility(View.VISIBLE);
            holder.tvSlotDesc.setVisibility(View.VISIBLE);
            holder.ivCardImage.setVisibility(View.VISIBLE);
            holder.ivDivider.setVisibility(ViewGroup.VISIBLE);
        } else {
            holder.tvSlotName.setVisibility(View.INVISIBLE);
            holder.tvSlotYear.setVisibility(View.INVISIBLE);
            holder.tvSlotDesc.setVisibility(View.INVISIBLE);
            holder.ivCardImage.setVisibility(View.INVISIBLE);
            holder.ivDivider.setVisibility(View.INVISIBLE);
            GradientDrawable border = new GradientDrawable();

            border.setStroke(2, Color.parseColor("#FFC107"), 8, 8);
            border.setCornerRadius(8);
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