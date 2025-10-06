package com.example.vietnamhistoryapplication.person.PersonList;

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
import com.example.vietnamhistoryapplication.person.PersonDetail.PersonDetailActivity;

import java.util.List;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {
    private List<PersonListItem> personListList;
    private Context context;
    private String periodSlug; // Thêm để truyền từ Activity

    public PersonListAdapter(List<PersonListItem> personListList, String periodSlug) {
        this.personListList = personListList;
        this.periodSlug = periodSlug;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvTitle;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.person_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonListItem personList = personListList.get(position);
        holder.tvName.setText(personList.name != null ? personList.name : "No Title");
        holder.tvDate.setText(personList.date != null ? personList.date : "No Date");
        holder.tvTitle.setText(personList.title != null ? personList.title : "No Title");

        if (personList.image != null) {
            ImageLoader.loadImage(holder.ivImage, personList.image);
        } else {
            holder.ivImage.setImageResource(R.drawable.background_1);
        }

        // Thêm sự kiện click để chuyển sang PersonDetailActivity (truyền periodSlug và personSlug)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PersonDetailActivity.class);
            intent.putExtra("PERIOD_SLUG", periodSlug); // Truyền period để load đúng subcollection
            intent.putExtra("PERSON_SLUG", personList.slug); // Truyền person slug
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return personListList.size();
    }
}