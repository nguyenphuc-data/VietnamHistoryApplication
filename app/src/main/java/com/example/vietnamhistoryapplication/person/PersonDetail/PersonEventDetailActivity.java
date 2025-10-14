package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.event.eventDetail.EventDetailActivity; // Import Activity mới
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonEventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String periodSlug, personSlug, eventSlug, eventRefPath;
    private ImageView ivBack, ivImage;
    private TextView tvTitle, tvOverview, tvRole, tvDescription;
    private Button btnEventDetails;

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
        btnEventDetails = findViewById(R.id.btnEventDetail);

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> onBackPressed());
        }

        if (btnEventDetails != null) {
            btnEventDetails.setOnClickListener(v -> {
                if (eventRefPath == null || eventRefPath.isEmpty()) {
                    Log.e("PersonEventDetailActivity", "eventRefPath is null or empty");
                    Toast.makeText(this, "Không tìm thấy tham chiếu sự kiện", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse eventRefPath: "periods/{period}/stages/{stage}/events/{event}"
                String normalizedPath = eventRefPath.startsWith("/") ? eventRefPath.substring(1) : eventRefPath;
                String[] pathParts = normalizedPath.split("/");

                Log.d("PersonEventDetailActivity", "Normalized eventRefPath: " + normalizedPath);

                if (pathParts.length < 6 ||
                        !pathParts[0].equals("periods") ||
                        !pathParts[2].equals("stages") ||
                        !pathParts[4].equals("events")) {
                    Log.e("PersonEventDetailActivity", "Invalid eventRefPath format: " + eventRefPath);
                    Toast.makeText(this, "Đường dẫn sự kiện không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                String periodSlugFromPath = pathParts[1];
                String stageSlugFromPath = pathParts[3];
                String eventSlugFromPath = pathParts[5];

                Log.d("PersonEventDetailActivity", "Parsed -> period: " + periodSlugFromPath +
                        ", stage: " + stageSlugFromPath + ", event: " + eventSlugFromPath);

                // Mở EventDetailActivity
                try {
                    Intent intent = new Intent(this, EventDetailActivity.class);
                    intent.putExtra("periodSlug", periodSlugFromPath);
                    intent.putExtra("stageSlug", stageSlugFromPath);
                    intent.putExtra("eventSlug", eventSlugFromPath);
                    startActivity(intent);

                    Log.d("PersonEventDetailActivity", "Started EventDetailActivity successfully");
                } catch (Exception e) {
                    Log.e("PersonEventDetailActivity", "Error starting EventDetailActivity: " + e.getMessage(), e);
                    Toast.makeText(this, "Lỗi mở chi tiết sự kiện", Toast.LENGTH_SHORT).show();
                }
            });
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
            Toast.makeText(this, "Không tìm thấy dữ liệu nhân vật", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PersonEventDetailActivity", "Loading event: " + eventSlug +
                " from period: " + periodSlug + ", person: " + personSlug);

        // Load dữ liệu sự kiện từ Firestore
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
                            // Hiển thị dữ liệu lên UI
                            tvTitle.setText(event.getTitle() != null ? event.getTitle() : "Không có tiêu đề");
                            tvOverview.setText(event.getOverview() != null ? event.getOverview() : "Không có tóm tắt");
                            tvRole.setText(event.getRole() != null ? event.getRole() : "Không có vai trò");
                            tvDescription.setText(event.getDescription() != null ? event.getDescription() : "Không có mô tả");

                            // Load ảnh
                            if (event.getCoverMediaRef() != null && !event.getCoverMediaRef().isEmpty()) {
                                ImageLoader.loadImage(ivImage, event.getCoverMediaRef());
                            } else {
                                ivImage.setImageResource(R.drawable.background_1);
                            }

                            // Lấy eventRefPath để mở chi tiết sự kiện
                            eventRefPath = event.getEventRef();
                            if (eventRefPath == null || eventRefPath.isEmpty()) {
                                Log.e("PersonEventDetailActivity", "eventRef is null or empty for event: " + eventSlug);
                                Toast.makeText(this, "Tham chiếu sự kiện không tồn tại", Toast.LENGTH_SHORT).show();
                                btnEventDetails.setEnabled(false);
                            } else {
                                Log.d("PersonEventDetailActivity", "Loaded event data successfully: " + event.getTitle() +
                                        ", eventRef: " + eventRefPath);
                                btnEventDetails.setEnabled(true);
                            }
                        } else {
                            Log.e("PersonEventDetailActivity", "Cannot parse event data for: " + eventSlug);
                            Toast.makeText(this, "Lỗi dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.e("PersonEventDetailActivity", "Document not found: " + eventSlug);
                        Toast.makeText(this, "Không tìm thấy dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonEventDetailActivity", "Error loading data: " + e.getMessage(), e);
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}