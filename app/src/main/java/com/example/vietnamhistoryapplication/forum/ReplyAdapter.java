package com.example.vietnamhistoryapplication.forum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.VH> {
    private final List<Reply> list;

    public ReplyAdapter(List<Reply> l) { list = l; }

    @NonNull @Override
    public VH onCreateViewHolder(ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.reply_item, p, false));
    }

    @Override
    public void onBindViewHolder(VH h, int p) {
        Reply r = list.get(p);
        h.tvAuthor.setText(r.authorName);
        h.tvContent.setText(r.content);

        if (r.createdAt != null) {
            h.tvTime.setText(new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault()).format(r.createdAt.toDate()));
        }

        if (r.authorPhoto != null && !r.authorPhoto.isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(r.authorPhoto)
                    .placeholder(R.drawable.avatar)
                    .into(h.ivAvatar);
        } else {
            h.ivAvatar.setImageResource(R.drawable.avatar);
        }
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar = itemView.findViewById(R.id.ivAvatar);
        TextView tvAuthor = itemView.findViewById(R.id.tvAuthor);
        TextView tvContent = itemView.findViewById(R.id.tvContent);
        TextView tvTime = itemView.findViewById(R.id.tvTime);
        VH(View v) { super(v); }
    }
}