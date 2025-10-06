package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;

import java.util.List;

public class PersonDetailLifetimeAdapter extends RecyclerView.Adapter<PersonDetailLifetimeAdapter.ViewHolder> {
    private List<String> lifetime;

    public PersonDetailLifetimeAdapter(List<String> lifetime) {
        this.lifetime = lifetime;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBulletItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBulletItem = itemView.findViewById(R.id.tvBulletItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_detail_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = lifetime.get(position);
        holder.tvBulletItem.setText("• " + item);
    }

    @Override
    public int getItemCount() {
        return lifetime.size();
    }
}