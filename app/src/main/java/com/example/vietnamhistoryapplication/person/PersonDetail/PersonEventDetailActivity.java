package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.event.eventDetail.EventDetailFragment;
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

                // ✅ Sửa lại phần xử lý eventRefPath
                // Ví dụ: "periods/1954-1975/stages/chong-my/events/tran-bien-gioi"
                String normalizedPath = eventRefPath.startsWith("/") ? eventRefPath.substring(1) : eventRefPath;
                String[] pathParts = normalizedPath.split("/");

                Log.d("PersonEventDetailActivity", "Normalized eventRefPath: " + normalizedPath);

                // Kiểm tra đúng cấu trúc: periods/{period}/stages/{stage}/events/{event}
                if (pathParts.length < 6 ||
                        !pathParts[0].equals("periods") ||
                        !pathParts[2].equals("stages") ||
                        !pathParts[4].equals("events")) {
                    Log.e("PersonEventDetailActivity", "Invalid eventRefPath format: " + eventRefPath);
                    Toast.makeText(this, "Đường dẫn sự kiện không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Trích xuất tham số chính xác
                String periodSlug = pathParts[1];
                String stageSlug = pathParts[3];
                String eventSlug = pathParts[5];

                Log.d("PersonEventDetailActivity", "Parsed -> periodSlug: " + periodSlug + ", stageSlug: " + stageSlug + ", eventSlug: " + eventSlug);

                // Khởi tạo và hiển thị EventDetailFragment
                EventDetailFragment fragment = new EventDetailFragment();
                Bundle args = new Bundle();
                args.putString(EventDetailFragment.ARG_PERIOD, periodSlug);
                args.putString(EventDetailFragment.ARG_STAGE, stageSlug);
                args.putString(EventDetailFragment.ARG_EVENT, eventSlug);
                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null); // Cho phép quay lại
                transaction.commit();

                // Hiển thị container
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
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

        Log.d("PersonDetailActivity", "Load event: " + eventSlug + " từ period: " + periodSlug + ", person: " + personSlug);

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

                            eventRefPath = event.getEventRef();
                            if (eventRefPath == null || eventRefPath.isEmpty()) {
                                Log.e("PersonEventDetailActivity", "eventRef is null or empty for event: " + eventSlug);
                                Toast.makeText(this, "Tham chiếu sự kiện không tồn tại", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("PersonEventDetailActivity", "Tải dữ liệu sự kiện thành công: " + event.getTitle() + ", eventRef: " + eventRefPath);
                            }
                        } else {
                            Log.e("PersonEventDetailActivity", "Không parse được dữ liệu sự kiện cho: " + eventSlug);
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
