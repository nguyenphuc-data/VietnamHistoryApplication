package com.example.vietnamhistoryapplication.forum;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vietnamhistoryapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnPost;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post_activity);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnPost = findViewById(R.id.btnPost);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnPost.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUid = user.getUid();
        String authorName = user.getDisplayName() != null ? user.getDisplayName() : "Người dùng";
        String authorPhoto = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("authorId", userUid);
        post.put("authorName", authorName);
        post.put("authorPhoto", authorPhoto);
        post.put("createdAt", FieldValue.serverTimestamp());
        post.put("replyCount", 0L);
        post.put("likeCount", 0L);
        post.put("likes", new ArrayList<String>());

        btnPost.setEnabled(false);
        btnPost.setText("Đang đăng...");

        // DÙNG .add() → AUTO-ID, KHÔNG TRÙNG!
        db.collection("forum").document("posts").collection("all")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnPost.setEnabled(true);
                    btnPost.setText("Đăng bài");
                });
    }
}