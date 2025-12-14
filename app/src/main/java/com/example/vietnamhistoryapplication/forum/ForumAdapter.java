package com.example.vietnamhistoryapplication.forum;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {
    private List<ForumPost> posts = new ArrayList<>();
    private Context context;

    public ForumAdapter(Context context) {
        this.context = context;
    }
    public void updateList(List<ForumPost> newList) {
        posts = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forum_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy bài đăng + UID người dùng hiện tại.
        ForumPost post = posts.get(position);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser != null ? currentUser.getUid() : "";

        holder.tvTitle.setText(post.title);
        holder.tvAuthor.setText(post.authorName);
        holder.tvTime.setText(formatTime(post.createdAt));
        holder.tvReplyCount.setText(post.replyCount + " trả lời");

        if (post.content != null && !post.content.trim().isEmpty()) {
            holder.tvContent.setText(post.content.trim());
            holder.tvContent.setVisibility(View.VISIBLE);
        } else {
            holder.tvContent.setVisibility(View.GONE);
        }

        if (post.authorPhoto != null && !post.authorPhoto.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.authorPhoto)
                    .placeholder(R.drawable.avatar)
                    .into(holder.ivAuthor);
        } else {
            holder.ivAuthor.setImageResource(R.drawable.avatar);
        }

        boolean isLiked = post.likes != null && post.likes.contains(currentUid);
        holder.ivLike.setImageResource(isLiked ? R.drawable.ic_like_filled : R.drawable.ic_like_outline);
        holder.tvLikeCount.setText(String.valueOf(post.likeCount));
        holder.tvLikeCount.setTextColor(isLiked ? 0xFFE8582B : 0xFF666666);

        holder.layoutLike.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(v.getContext(), "Đăng nhập để like!", Toast.LENGTH_SHORT).show();
                return;
            }
            toggleLike(post, holder, v.getContext());
        });
        // hiện menu (3 chấm) nếu là người đăng bài
        if (currentUid.equals(post.authorId)) {
            holder.ivMenu.setVisibility(View.VISIBLE);
            holder.ivMenu.setOnClickListener(v -> showPostMenu(v, post));
        } else {
            holder.ivMenu.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ForumDetailActivity.class);
            intent.putExtra("postId", post.postId);
            context.startActivity(intent);
        });
    }

    private void toggleLike(ForumPost post, ViewHolder holder, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("forum").document("posts").collection("all").document(post.postId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(ref);
            ForumPost current = snapshot.toObject(ForumPost.class);
            if (current == null) return null;

            if (current.likes == null) current.likes = new ArrayList<>();

            if (current.likes.contains(uid)) {
                current.likes.remove(uid);
                current.likeCount--;
            } else {
                current.likes.add(uid);
                current.likeCount++;
            }

            transaction.set(ref, current);
            return null;
        }).addOnSuccessListener(aVoid -> {
            boolean isLiked = post.likes.contains(uid);
            holder.ivLike.setImageResource(isLiked ? R.drawable.ic_like_filled : R.drawable.ic_like_outline);
            holder.tvLikeCount.setText(String.valueOf(post.likeCount));
            holder.tvLikeCount.setTextColor(isLiked ? 0xFFE8582B : 0xFF666666);
        });
    }

    private void showPostMenu(View v, ForumPost post) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.menu_post);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                Intent intent = new Intent(v.getContext(), EditPostActivity.class);
                intent.putExtra("postId", post.postId);
                v.getContext().startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                deletePost(post, v.getContext());
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void deletePost(ForumPost post, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String postId = post.postId;

        db.collection("forum").document("posts").collection("all").document(postId)
                .collection("replies")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit().addOnCompleteListener(task -> {
                        db.collection("forum").document("posts").collection("all").document(postId).delete()
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show());
                    });
                });
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "Vừa xong";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    @Override
    public int getItemCount() { return posts.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthor;
        TextView tvTitle, tvAuthor, tvTime, tvContent, tvReplyCount, tvLikeCount;
        ImageView ivLike, ivMenu;
        LinearLayout layoutLike;

        ViewHolder(View itemView) {
            super(itemView);
            ivAuthor = itemView.findViewById(R.id.ivAuthor);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivMenu = itemView.findViewById(R.id.ivMenu);
            layoutLike = itemView.findViewById(R.id.layoutLike);
        }
    }
}