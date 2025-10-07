package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private TextView tvAchievements;
    private TextView tvLifetime;

    // Expandable headers và arrows
    private LinearLayout layoutAchievementsHeader;
    private ImageView ivAchievementArrow;
    private LinearLayout layoutLifetimeHeader;
    private ImageView ivLifetimeArrow;

    private boolean isAchievementExpanded = false;
    private boolean isLifetimeExpanded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_detail_activity);

        initViews();
        setupClickListeners();
        loadPersonData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvAchievements = findViewById(R.id.tvAchievements);
        tvLifetime = findViewById(R.id.tvLifetime);

        // Expandable
        layoutAchievementsHeader = findViewById(R.id.layout_achievements_header);
        ivAchievementArrow = findViewById(R.id.ivAchievementArrow);
        layoutLifetimeHeader = findViewById(R.id.layout_lifetime_header);
        ivLifetimeArrow = findViewById(R.id.ivLifetimeArrow);

        // Nút back
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupClickListeners() {
        layoutAchievementsHeader.setOnClickListener(v -> {
            Log.d("PersonDetailActivity", "Click Thành tựu header");
            toggleSection(tvAchievements, ivAchievementArrow, isAchievementExpanded, value -> isAchievementExpanded = value);
        });
        layoutLifetimeHeader.setOnClickListener(v -> {
            Log.d("PersonDetailActivity", "Click Tóm tắt cuộc đời header");
            toggleSection(tvLifetime, ivLifetimeArrow, isLifetimeExpanded, value -> isLifetimeExpanded = value);
        });
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadPersonData() {
        setupFirebase();
        periodSlug = getIntent().getStringExtra("period_slug");
        personSlug = getIntent().getStringExtra("person_slug");

        if (periodSlug == null || personSlug == null || periodSlug.isEmpty() || personSlug.isEmpty()) {
            Log.e("PersonDetailActivity", "Thiếu period_slug hoặc person_slug");
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

        // Thành tựu: Join list thành string với bullet (set text sau toggle nếu cần)
        List<String> achievementsList = person.achievements != null ? person.achievements : new ArrayList<>();
        String achievementsText = achievementsList.stream()
                .map(s -> "• " + s)
                .collect(Collectors.joining("\n"));
        tvAchievements.setText(achievementsText.isEmpty() ? "Không có thành tựu" : achievementsText);

        // Tóm tắt cuộc đời: Join list thành string với bullet
        List<String> lifetimeList = person.lifetime != null ? person.lifetime : new ArrayList<>();
        String lifetimeText = lifetimeList.stream()
                .map(s -> "• " + s)
                .collect(Collectors.joining("\n"));
        tvLifetime.setText(lifetimeText.isEmpty() ? "Không có thông tin" : lifetimeText);

        Log.d("PersonDetailActivity", "Bind data thành công: " + person.name + ", achievements: " + achievementsList.size() + ", lifetime: " + lifetimeList.size());
    }

    // Toggle section (TextView + arrow)
    private void toggleSection(TextView content, ImageView arrow, boolean isExpanded, java.util.function.Consumer<Boolean> stateUpdater) {
        boolean newExpanded = !isExpanded;

        // Swap arrow icon
        int newResId = newExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down;
        arrow.setImageResource(newResId);
        arrow.setRotation(0f);  // Reset

        // Animation xoay nhẹ
        ObjectAnimator animator = ObjectAnimator.ofFloat(arrow, "rotation", 0f, newExpanded ? 180f : 0f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        // Fade content
        content.animate()
                .alpha(newExpanded ? 1f : 0f)
                .setDuration(300)
                .withEndAction(() -> {
                    content.setVisibility(newExpanded ? View.VISIBLE : View.GONE);
                    Log.d("PersonDetailActivity", "Toggle " + (newExpanded ? "expanded" : "collapsed") + " section");
                })
                .start();

        // Update state
        stateUpdater.accept(newExpanded);
    }
}