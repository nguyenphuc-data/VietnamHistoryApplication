package com.example.vietnamhistoryapplication.home.PeriodFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

//import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;

import java.util.List;
import androidx.viewpager2.widget.ViewPager2;
public class PeriodAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<PeriodAdapter.ViewHolder> {
    private List<Period> periods;
    private final OnPeriodClickListener clickListener;

    public interface OnPeriodClickListener {
        void onPeriodClick(Period period);
    }

    public PeriodAdapter(List<Period> periods, OnPeriodClickListener clickListener) {
        this.periods = periods;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.periods_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Period period = periods.get(position);
        holder.tvTitle.setText(period.title != null ? period.title : "No Title");
        holder.tvPeriod.setText(period.periodRange != null ? period.periodRange : "No Period");
        holder.tvSummary.setText(period.summary != null ? period.summary : "No Description");

        ImageLoader.loadImage(holder.ivImage, period.image);


        holder.itemView.setOnClickListener(v -> clickListener.onPeriodClick(period));

    }

    @Override
    public int getItemCount() {
        return periods.size();
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView tvTitle, tvPeriod, tvSummary;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}