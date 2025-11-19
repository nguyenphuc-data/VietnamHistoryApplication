package com.example.vietnamhistoryapplication.explore.articleDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.explore.articleDetail.Event;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private final Context context;
    private final List<Event> events;

    public ArticleAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.tvEventDetails.setText(event.getDetails());
        holder.tvImageCaption.setText(event.getImage().getContent());

        String imageUrl = event.getImage().getLink();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).placeholder(R.drawable.placeholder).into(holder.imgEvent);
        } else {
            holder.imgEvent.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventDetails, tvImageCaption;
        ImageView imgEvent;

        ViewHolder(View itemView) {
            super(itemView);
            tvEventDetails = itemView.findViewById(R.id.tvEventDetails);
            imgEvent = itemView.findViewById(R.id.imgEventArticle);
            tvImageCaption = itemView.findViewById(R.id.tvImageCaption);
        }
    }
}
