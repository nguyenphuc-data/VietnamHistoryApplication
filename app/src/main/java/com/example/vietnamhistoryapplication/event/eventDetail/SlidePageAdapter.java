package com.example.vietnamhistoryapplication.event.eventDetail;

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
import java.util.Map;

public class SlidePageAdapter extends RecyclerView.Adapter<SlidePageAdapter.ViewHolder> {
    private List<Map<String, Object>> slides;
    private Context context;

    public SlidePageAdapter(List<Map<String, Object>> slides, Context context) {
        this.slides = slides;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_detail_item_slide_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> slide = slides.get(position);
        String link = (String) slide.get("link");
//        String caption = (String) slide.get("caption");
        String desc = (String) slide.get("description");

        ImageLoader.loadImage(holder.ivSlideImage, link);
//        holder.tvSlideTitle.setText(caption);
        holder.tvSlideDesc.setText(desc);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSlideImage;
        TextView  tvSlideDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSlideImage = itemView.findViewById(R.id.ivSlideImage);
//            tvSlideTitle = itemView.findViewById(R.id.tvSlideTitle);
            tvSlideDesc = itemView.findViewById(R.id.tvSlideDescription);
        }
    }
}