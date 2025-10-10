package com.example.vietnamhistoryapplication.person.PersonDetail;

import android.animation.ObjectAnimator;
import android.content.Intent;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersonDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String periodSlug, personSlug;
    private ImageView ivBack, ivImage, ivAchievementArrow, ivLifetimeArrow, ivEventsArrow;
    private TextView tvName, tvTitle, tvOverview, tvAchievements, tvLifetime;
    private LinearLayout layoutAchievementsHeader, layoutLifetimeHeader, layoutEventsHeader, eventsContainer;
    private List<EventItem> events = new ArrayList<>();
    private boolean isAchievementExpanded = false;
    private boolean isLifetimeExpanded = false;
    private boolean isEventsExpanded = false;

    private static class EventItem {
        private String slug;
        private String title;

        public EventItem(String slug, String title) {
            this.slug = slug;
            this.title = title;
        }

        public String getSlug() {
            return slug;
        }

        public String getTitle() {
            return title;
        }
    }

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
        layoutAchievementsHeader = findViewById(R.id.layout_achievements_header);
        layoutLifetimeHeader = findViewById(R.id.layout_lifetime_header);
        layoutEventsHeader = findViewById(R.id.layout_events_header);
        ivAchievementArrow = findViewById(R.id.ivAchievementArrow);
        ivLifetimeArrow = findViewById(R.id.ivLifetimeArrow);
        ivEventsArrow = findViewById(R.id.ivEventsArrow);
        eventsContainer = findViewById(R.id.events_container);

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
        layoutEventsHeader.setOnClickListener(v -> {
            Log.d("PersonDetailActivity", "Click Sự kiện tham gia header");
            toggleSection(eventsContainer, ivEventsArrow, isEventsExpanded, value -> isEventsExpanded = value);
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

        db.collection("periods_person")
                .document(periodSlug)
                .collection("persons")
                .document(personSlug)
                .collection("events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Log.d("PersonDetailActivity", "Không tìm thấy sự kiện cho nhân vật: " + personSlug);
                        TextView noEventsText = new TextView(this);
                        noEventsText.setText("Không có sự kiện nào");
                        noEventsText.setTextSize(14);
                        noEventsText.setPadding(0, 0, 0, 16);
                        eventsContainer.addView(noEventsText);
                    } else {
                        events.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String slug = doc.getId();
                            String title = doc.getString("title");

                            EventItem eventItem = new EventItem(slug, title);
                            events.add(eventItem);

                            TextView eventTextView = new TextView(this);
                            eventTextView.setText("• " + title);
                            eventTextView.setTextSize(14);
                            eventTextView.setPadding(0, 8, 0, 8);
                            eventTextView.setTextColor(getResources().getColor(android.R.color.black));
                            eventTextView.setClickable(true);
                            eventTextView.setFocusable(true);
                            eventTextView.setBackgroundResource(android.R.drawable.list_selector_background);

                            eventTextView.setOnClickListener(v -> {
                                Intent intent = new Intent(PersonDetailActivity.this, PersonEventDetailActivity.class);
                                intent.putExtra("eventSlug", eventItem.getSlug());
                                intent.putExtra("periodSlug", periodSlug);
                                intent.putExtra("personSlug", personSlug);
                                startActivity(intent);
                            });

                            eventsContainer.addView(eventTextView);
                        }
                        Log.d("PersonDetailActivity", "Đã tải " + events.size() + " sự kiện cho nhân vật: " + personSlug);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonDetailActivity", "Lỗi tải sự kiện: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải dữ liệu sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void bindData(PersonDetailItem person) {
        tvName.setText(person.name != null ? person.name : "Không có tên");
        tvTitle.setText(person.title != null ? person.title : "Không có tiêu đề");

        String overviewText = person.overview != null ? person.overview :
                (person.hometown != null ? person.hometown + ", sinh " + person.birth_year + "–mất " + person.death_year : "Không có thông tin");
        tvOverview.setText(overviewText);

        if (person.horizontalImage != null && !person.horizontalImage.isEmpty()) {
            ImageLoader.loadImage(ivImage, person.horizontalImage);
        } else {
            ivImage.setImageResource(R.drawable.background_1);
        }

        List<String> achievementsList = person.achievements != null ? person.achievements : new ArrayList<>();
        String achievementsText = achievementsList.stream()
                .map(s -> "• " + s)
                .collect(Collectors.joining("\n"));
        tvAchievements.setText(achievementsText.isEmpty() ? "Không có thành tựu" : achievementsText);

        List<String> lifetimeList = person.lifetime != null ? person.lifetime : new ArrayList<>();
        String lifetimeText = lifetimeList.stream()
                .map(s -> "• " + s)
                .collect(Collectors.joining("\n"));
        tvLifetime.setText(lifetimeText.isEmpty() ? "Không có thông tin" : lifetimeText);

        Log.d("PersonDetailActivity", "Bind data thành công: " + person.name + ", thành tựu: " + achievementsList.size() + ", tóm tắt cuộc đời: " + lifetimeList.size());
    }

    private void toggleSection(View content, ImageView arrow, boolean isExpanded, java.util.function.Consumer<Boolean> stateUpdater) {
        boolean newExpanded = !isExpanded;

        int newResId = newExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down;
        arrow.setImageResource(newResId);
        arrow.setRotation(0f);

        ObjectAnimator animator = ObjectAnimator.ofFloat(arrow, "rotation", 0f, newExpanded ? 180f : 0f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        content.animate()
                .alpha(newExpanded ? 1f : 0f)
                .setDuration(300)
                .withEndAction(() -> {
                    content.setVisibility(newExpanded ? View.VISIBLE : View.GONE);
                    Log.d("PersonDetailActivity", "Toggle " + (newExpanded ? "mở rộng" : "thu gọn") + " section");
                })
                .start();

        stateUpdater.accept(newExpanded);
    }
}