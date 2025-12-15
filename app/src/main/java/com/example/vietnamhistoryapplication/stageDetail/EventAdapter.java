package com.example.vietnamhistoryapplication.stageDetail;

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
import com.example.vietnamhistoryapplication.event.main.EventHomeActivity;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<EventItem> eventList;
    private String periodSlug;
    private String stageSlug;
    private Context context;

    public EventAdapter(List<EventItem> eventList, String periodSlug, String stageSlug) {
        this.eventList = eventList;
        this.periodSlug = periodSlug;
        this.stageSlug = stageSlug;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.events_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem event = eventList.get(position);

        holder.tvsortOrder.setText(event.sortOrder != null ? event.sortOrder.toString() : "0");
        holder.tvTitle.setText(event.title != null ? event.title : "No Title");
        holder.tvPeriod.setText(event.dateRange != null ? event.dateRange : "No Period");
        holder.tvsmallTitle.setText(event.smallTitle != null ? event.smallTitle : "No Description");
        holder.tvType.setText(event.type != null ? event.type : "No Type");
        ImageLoader.loadImage(holder.ivImage, event.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context,EventHomeActivity.class);
            intent.putExtra("periodSlug", periodSlug);
            intent.putExtra("stageSlug", stageSlug);
            intent.putExtra("eventSlug", event.slug);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvsortOrder, tvTitle, tvPeriod, tvsmallTitle, tvType;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvsortOrder = itemView.findViewById(R.id.tvsortOrder);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvsmallTitle = itemView.findViewById(R.id.tvsmallTitle);
            tvType = itemView.findViewById(R.id.tvType);
            ivImage = itemView.findViewById(R.id.ivEventImage);
        }
    }
}
