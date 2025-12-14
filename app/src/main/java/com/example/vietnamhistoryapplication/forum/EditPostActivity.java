package com.example.vietnamhistoryapplication.forum;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vietnamhistoryapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnSave;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_post_activity);

        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            finish();
            return;
        }

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);

        loadPost();
        btnSave.setOnClickListener(v -> savePost());
    }

    private void loadPost() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("forum").document("posts").collection("all").document(postId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etTitle.setText(doc.getString("title"));
                        etContent.setText(doc.getString("content"));
                    }
                });
    }

    private void savePost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("content", content);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("forum").document("posts").collection("all").document(postId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}