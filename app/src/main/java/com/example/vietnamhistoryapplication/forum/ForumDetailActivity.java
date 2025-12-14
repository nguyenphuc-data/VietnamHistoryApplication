package com.example.vietnamhistoryapplication.forum;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ForumDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvTime, tvContent, tvNoReplies;
    private EditText etReply;
    private FloatingActionButton btnSend;
    private RecyclerView rvReplies;
    private ReplyAdapter replyAdapter;
    private final List<Reply> replyList = new ArrayList<>();
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_detail_activity);

        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadPostDetail();
        loadReplies();
    }

    private void initViews() {
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        TextView tvTitleBar = findViewById(R.id.tvTitleBar);
        tvTitleBar.setText("Chi tiết câu hỏi");

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvTime = findViewById(R.id.tvTime);
        tvContent = findViewById(R.id.tvContent);
        tvNoReplies = findViewById(R.id.tvNoReplies);

        etReply = findViewById(R.id.etReply);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendReply());

        rvReplies = findViewById(R.id.rvReplies);
        rvReplies.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(replyList);
        rvReplies.setAdapter(replyAdapter);
    }

    private void loadPostDetail() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("forum").document("posts").collection("all").document(postId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ForumPost post = doc.toObject(ForumPost.class);
                        if (post != null) {
                            tvTitle.setText(post.title);
                            tvAuthor.setText(post.authorName);
                            tvContent.setText(post.content);
                            tvTime.setText(formatTime(post.createdAt));

                            CircleImageView ivAuthor = findViewById(R.id.ivAuthor);
                            if (post.authorPhoto != null && !post.authorPhoto.isEmpty()) {
                                Glide.with(this)
                                        .load(post.authorPhoto)
                                        .placeholder(R.drawable.avatar)
                                        .into(ivAuthor);
                            } else {
                                ivAuthor.setImageResource(R.drawable.avatar);
                            }
                        }
                    }
                });
    }

    private void loadReplies() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("forum").document("posts").collection("all").document(postId)
                .collection("replies")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;

                    replyList.clear();
                    for (DocumentSnapshot d : snapshots.getDocuments()) {
                        Reply r = d.toObject(Reply.class);
                        if (r != null) replyList.add(r);
                    }

                    replyAdapter.notifyDataSetChanged();

                    if (replyList.isEmpty()) {
                        rvReplies.setVisibility(View.GONE);
                        tvNoReplies.setVisibility(View.VISIBLE);
                    } else {
                        rvReplies.setVisibility(View.VISIBLE);
                        tvNoReplies.setVisibility(View.GONE);
                    }
                });
    }

    private void sendReply() {
        String content = etReply.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung trả lời!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để trả lời!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userUid).get()
                .addOnSuccessListener(doc -> {
                    String authorName = doc.exists() && doc.getString("name") != null
                            ? doc.getString("name") : "Người dùng";
                    String authorPhoto = doc.exists() && doc.getString("photo") != null
                            ? doc.getString("photo") : "";

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("content", content);
                    map.put("authorId", userUid);
                    map.put("authorName", authorName);
                    map.put("authorPhoto", authorPhoto);
                    map.put("createdAt", FieldValue.serverTimestamp());

                    btnSend.setEnabled(false);

                    db.collection("forum").document("posts").collection("all").document(postId)
                            .collection("replies")
                            .add(map)
                            .addOnSuccessListener(d -> {
                                etReply.setText("");
                                btnSend.setEnabled(true);

                                db.collection("forum").document("posts").collection("all").document(postId)
                                        .update("replyCount", FieldValue.increment(1));
                            })
                            .addOnFailureListener(e2 -> {
                                btnSend.setEnabled(true);
                                Toast.makeText(this, "Gửi thất bại: " + e2.getMessage(), Toast.LENGTH_LONG).show();
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
}