package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PersonDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String periodSlug;
    private String personSlug;

    // Views
    private ImageView ivBack;
    private ImageView ivImage;
    private TextView tvName;
    private TextView tvTitle;
    private TextView tvOverview;
    private RecyclerView rvAchievements;
    private RecyclerView rvLifetime;

    // Headers (để giữ bố cục)
    private LinearLayout layoutAchievementsHeader;
    private LinearLayout layoutLifetimeHeader;

    private PersonDetailAchievementAdapter achievementAdapter;
    private PersonDetailLifetimeAdapter lifetimeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_detail_activity);

        initViews();
        loadPersonData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        rvAchievements = findViewById(R.id.rvAchievements);
        rvLifetime = findViewById(R.id.rvLifetime);

        layoutAchievementsHeader = findViewById(R.id.layout_achievements_header);
        layoutLifetimeHeader = findViewById(R.id.layout_lifetime_header);

        rvAchievements.setLayoutManager(new LinearLayoutManager(this));
        rvLifetime.setLayoutManager(new LinearLayoutManager(this));

        // Hiển thị mặc định tất cả (fix: dùng View.VISIBLE, alpha không cần)
        rvAchievements.setVisibility(View.VISIBLE);
        rvLifetime.setVisibility(View.VISIBLE);

        // Nút back
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadPersonData() {
        setupFirebase();
        periodSlug = getIntent().getStringExtra("PERIOD_SLUG");
        personSlug = getIntent().getStringExtra("PERSON_SLUG");

        if (periodSlug == null || personSlug == null || periodSlug.isEmpty() || personSlug.isEmpty()) {
            Log.e("PersonDetailActivity", "Thiếu PERIOD_SLUG hoặc PERSON_SLUG");
            Toast.makeText(this, "Không tìm thấy dữ liệu nhân vật", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PersonDetailActivity", "Load person: " + personSlug + " từ period: " + periodSlug);

        db.collection("periods_person")
                .document(periodSlug)
                .collection("persons")
                .document(personSlug)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PersonDetailItem person = documentSnapshot.toObject(PersonDetailItem.class);
                        if (person != null) {
                            bindData(person);
                        } else {
                            Log.e("PersonDetailActivity", "Không parse được data");
                            Toast.makeText(this, "Lỗi dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("PersonDetailActivity", "Không tìm thấy document: " + personSlug);
                        Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonDetailActivity", "Lỗi Firestore: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void bindData(PersonDetailItem person) {
        tvName.setText(person.name != null ? person.name : "No Name");
        tvTitle.setText(person.title != null ? person.title : "No Title");

        // Overview
        String overviewText = person.overview != null ? person.overview :
                (person.hometown != null ? person.hometown + ", sinh " + person.birth_year + "–mất " + person.death_year : "No Overview");
        tvOverview.setText(overviewText);

        // Ảnh lớn: horizontalImage (nếu có)
        if (person.horizontalImage != null && !person.horizontalImage.isEmpty()) {
            ImageLoader.loadImage(ivImage, person.horizontalImage);
        } else {
            ivImage.setImageResource(R.drawable.background_1); // Placeholder
        }

        // Thành tựu
        List<String> achievementsList = person.achievements != null ? person.achievements : new ArrayList<>();
        achievementAdapter = new PersonDetailAchievementAdapter(achievementsList);
        rvAchievements.setAdapter(achievementAdapter);
        achievementAdapter.notifyDataSetChanged(); // Fix: Đảm bảo update UI ngay

        // Tóm tắt cuộc đời
        List<String> lifetimeList = person.lifetime != null ? person.lifetime : new ArrayList<>();
        lifetimeAdapter = new PersonDetailLifetimeAdapter(lifetimeList);
        rvLifetime.setAdapter(lifetimeAdapter);
        lifetimeAdapter.notifyDataSetChanged(); // Fix: Đảm bảo update UI ngay

        Log.d("PersonDetailActivity", "Bind data thành công: " + person.name + ", achievements: " + achievementsList.size() + ", lifetime: " + lifetimeList.size());
    }
}