package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonEventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String periodSlug, personSlug, eventSlug;
    private ImageView ivBack, ivImage;
    private TextView tvTitle, tvOverview, tvRole, tvDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_event_detail_activity);

        initViews();
        setupFirebase();
        loadEventData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivImage = findViewById(R.id.ivImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvRole = findViewById(R.id.tvRole);
        tvDescription = findViewById(R.id.tvDescription);

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadEventData() {
        periodSlug = getIntent().getStringExtra("periodSlug");
        personSlug = getIntent().getStringExtra("personSlug");
        eventSlug = getIntent().getStringExtra("eventSlug");

        if (periodSlug == null || personSlug == null || eventSlug == null ||
                periodSlug.isEmpty() || personSlug.isEmpty() || eventSlug.isEmpty()) {
            Log.e("PersonEventDetailActivity", "Thiếu periodSlug, personSlug hoặc eventSlug");
            Toast.makeText(this, "Không tìm thấy dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PersonEventDetailActivity", "Tải dữ liệu sự kiện: " + eventSlug);

        db.collection("periods_person")
                .document(periodSlug)
                .collection("persons")
                .document(personSlug)
                .collection("events")
                .document(eventSlug)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PersonEventDetailItem event = documentSnapshot.toObject(PersonEventDetailItem.class);
                        if (event != null) {
                            tvTitle.setText(event.getTitle() != null ? event.getTitle() : "Không có tiêu đề");
                            tvOverview.setText(event.getOverview() != null ? event.getOverview() : "Không có tóm tắt");
                            tvRole.setText(event.getRole() != null ? event.getRole() : "Không có vai trò");
                            tvDescription.setText(event.getDescription() != null ? event.getDescription() : "Không có mô tả");

                            if (event.getCoverMediaRef() != null && !event.getCoverMediaRef().isEmpty()) {
                                ImageLoader.loadImage(ivImage, event.getCoverMediaRef());
                            } else {
                                ivImage.setImageResource(R.drawable.background_1);
                            }

                            Log.d("PersonEventDetailActivity", "Tải dữ liệu sự kiện thành công: " + event.getTitle());
                        } else {
                            Log.e("PersonEventDetailActivity", "Không parse được dữ liệu sự kiện");
                            Toast.makeText(this, "Lỗi dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.e("PersonEventDetailActivity", "Không tìm thấy document: " + eventSlug);
                        Toast.makeText(this, "Không tìm thấy dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonEventDetailActivity", "Lỗi tải dữ liệu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}