package com.example.vietnamhistoryapplication.home.ExploreFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.home.ExploreFragment.Explore;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private final List<Explore> items;
    private final OnExploreClickListener listener;

    public interface OnExploreClickListener {
        void onExploreClick(Explore item);
    }

    public ExploreAdapter(List<Explore> items, OnExploreClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_explore_item_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Explore item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBackground;
        TextView tvTitle, tvDesc;
        ImageButton btnArrow;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBackground = itemView.findViewById(R.id.imgBackground);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            btnArrow = itemView.findViewById(R.id.btnArrow);
        }

        void bind(Explore item) {
            tvTitle.setText(item.getTitle());
            tvDesc.setText(item.getDescription());

            // Load ảnh nền
            if (item.getCoverMediaRef() != null && !item.getCoverMediaRef().isEmpty()) {
                ImageLoader.loadImage(imgBackground, item.getCoverMediaRef());
            } else {
                imgBackground.setImageResource(R.color.quiz_option_background);
            }

            // Click vào mũi tên → mở chi tiết
            btnArrow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExploreClick(item);
                }
            });

            // Click toàn bộ card cũng mở chi tiết
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExploreClick(item);
                }
            });
        }
    }
}