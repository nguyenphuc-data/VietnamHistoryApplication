package com.example.vietnamhistoryapplication.forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;
import com.example.vietnamhistoryapplication.R;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView rvForum;
    private ForumAdapter forumAdapter;
    private ExtendedFloatingActionButton fabPost;
    private TextView tvNoPosts;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<ForumPost> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadPosts();
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    private void initViews() {
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        fabPost = findViewById(R.id.fabPost);
        fabPost.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để hỏi!", Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(this, NewPostActivity.class));
        });

        tvNoPosts = findViewById(R.id.tvNoPosts);
    }

    private void setupRecyclerView() {
        rvForum = findViewById(R.id.rvForum);
        rvForum.setLayoutManager(new LinearLayoutManager(this));
        forumAdapter = new ForumAdapter(this);
        rvForum.setAdapter(forumAdapter);
    }

    private void loadPosts() {
        db.collection("forum").document("posts").collection("all")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    postList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        ForumPost post = doc.toObject(ForumPost.class); // ánh xạ dữ liệu Firestore -> đối tượng Java
                        if (post != null) {
                            post.postId = doc.getId();
                            postList.add(post);
                        }
                    }

                    forumAdapter.updateList(postList);

                    if (postList.isEmpty()) {
                        rvForum.setVisibility(View.GONE);
                        tvNoPosts.setVisibility(View.VISIBLE);
                    } else {
                        rvForum.setVisibility(View.VISIBLE);
                        tvNoPosts.setVisibility(View.GONE);
                    }
                });
    }
}