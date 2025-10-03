package com.example.vietnamhistoryapplication.stageDetail;

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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<EventItem> eventList;

    public EventAdapter(List<EventItem> eventList) {
        this.eventList = eventList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
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
    }
    @Override
    public int getItemCount() {
        return eventList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvsortOrder,tvTitle,tvPeriod,tvsmallTitle,tvType;
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
