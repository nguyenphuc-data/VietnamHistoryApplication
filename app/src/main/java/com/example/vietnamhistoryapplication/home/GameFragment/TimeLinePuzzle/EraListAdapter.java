package com.example.vietnamhistoryapplication.home.GameFragment.TimeLinePuzzle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.models.Era;

import java.util.List;

public class EraListAdapter extends RecyclerView.Adapter<EraListAdapter.ViewHolder> {

    private List<Era> eraList;
    private OnEraClickListener clickListener;

    public interface OnEraClickListener {
        void onEraClick(Era era);
    }

    public EraListAdapter(List<Era> eraList, OnEraClickListener clickListener) {
        this.eraList = eraList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.era_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Era era = eraList.get(position);

        holder.tvEraName.setText(era.getName());
        holder.tvShortDesc.setText(era.getShortDesc());

        // Load ảnh bằng Glide
        ImageLoader.loadImage(holder.imgThumbnail, era.getThumbnailUrl());

        holder.btnPlay.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onEraClick(era);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eraList != null ? eraList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvEraName, tvShortDesc;
        Button btnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvEraName = itemView.findViewById(R.id.tvEraName);
            tvShortDesc = itemView.findViewById(R.id.tvShortDesc);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}