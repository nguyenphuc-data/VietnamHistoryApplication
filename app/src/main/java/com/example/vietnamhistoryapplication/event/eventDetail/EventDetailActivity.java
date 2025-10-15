package com.example.vietnamhistoryapplication.event.eventDetail;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.YouTubeUtils;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.EventDetailItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.HeaderItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.IntroItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.SectionListItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.SectionListItem2;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.SlidesItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.VideoItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    private final ArrayList<EventDetailItem> items = new ArrayList<>();
    private EventDetailAdapter adapter;

    private String periodSlug, stageSlug, eventSlug;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_fragment);

        ImageView ivBack = findViewById(R.id.ivBack);
        RecyclerView rv = findViewById(R.id.recycler_view_event_details);

        ivBack.setOnClickListener(v -> onBackPressed());

        periodSlug = getIntent().getStringExtra("periodSlug");
        stageSlug = getIntent().getStringExtra("stageSlug");
        eventSlug = getIntent().getStringExtra("eventSlug");

        Log.d("EventDetailActivity", "periodSlug: " + periodSlug);
        Log.d("EventDetailActivity", "stageSlug: " + stageSlug);
        Log.d("EventDetailActivity", "eventSlug: " + eventSlug);

        if (TextUtils.isEmpty(periodSlug) || TextUtils.isEmpty(stageSlug) || TextUtils.isEmpty(eventSlug)) {
            Toast.makeText(this, "Thiếu tham số event (period/stage/event).", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventDetailAdapter(items, (LifecycleOwner) this);
        rv.setAdapter(adapter);

        loadFromFirestore(periodSlug, stageSlug, eventSlug);
    }

    @SuppressWarnings("unchecked")
    private void loadFromFirestore(String periodSlug, String stageSlug, String eventSlug) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("periods")
                .document(periodSlug)
                .collection("stages")
                .document(stageSlug)
                .collection("events")
                .document(eventSlug)
                .get()
                .addOnSuccessListener(this::mapDocToUi)
                .addOnFailureListener(e -> {
                    Log.e("EventDetailActivity", "Firestore load failed: " + e.getMessage());
                    Toast.makeText(this, "Không tải được dữ liệu.", Toast.LENGTH_LONG).show();
                });
    }

    @SuppressWarnings("unchecked")
    private void mapDocToUi(DocumentSnapshot doc) {
        items.clear();

        String title = doc.getString("title");
        String smallTitle = doc.getString("smallTitle");
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title != null ? title : "Tiêu đề không có");

        // HEADER
        List<Map<String, Object>> images = (List<Map<String, Object>>) doc.get("images");
        String cover = doc.getString("coverMediaRef");

        // Debug images
        Log.d("EventDetailActivity", "Images list size: " + (images != null ? images.size() : "null"));
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                Log.d("EventDetailActivity", "Image[" + i + "]: " + images.get(i));
            }
        }

        // Fallback cho cover image
        if (cover == null && images != null && !images.isEmpty()) {
            Object l = images.get(0).get("link");
            if (l != null) {
                cover = String.valueOf(l);
                Log.d("EventDetailActivity", "Using cover from images[0]: " + cover);
            }
        }

        items.add(new HeaderItem(
                TextUtils.isEmpty(title) ? "" : title,
                TextUtils.isEmpty(smallTitle) ? "" : smallTitle,
                cover
        ));

        // INTRO (summary + ảnh tài liệu) - ✅ ĐÃ SỬA LỖI INDEX OUT OF BOUNDS
        String overview = doc.getString("summary");
        String docImage = cover; // Default là cover image

        // Safe access cho ảnh tài liệu (index 3)
        if (images != null && images.size() > 3) {
            // Có đủ 4 ảnh trở lên, dùng ảnh thứ 4 (index 3)
            Object docImgObj = images.get(3).get("link");
            if (docImgObj != null) {
                docImage = String.valueOf(docImgObj);
                Log.d("EventDetailActivity", "Using docImage from images[3]: " + docImage);
            }
        } else if (images != null && images.size() > 1) {
            // Chỉ có 2-3 ảnh, dùng ảnh thứ 2 (index 1) làm fallback
            Object fallbackImg = images.get(1).get("link");
            if (fallbackImg != null) {
                docImage = String.valueOf(fallbackImg);
                Log.d("EventDetailActivity", "Using fallback docImage from images[1]: " + docImage);
            }
        } else if (images != null && !images.isEmpty()) {
            // Chỉ có 1 ảnh, dùng ảnh đầu tiên
            Object singleImg = images.get(0).get("link");
            if (singleImg != null) {
                docImage = String.valueOf(singleImg);
                Log.d("EventDetailActivity", "Using single docImage from images[0]: " + docImage);
            }
        } else {
            Log.w("EventDetailActivity", "No images available, using cover: " + cover);
        }

        items.add(new IntroItem(
                TextUtils.isEmpty(overview) ? "" : overview,
                docImage
        ));

        // LÍ DO (warCause)
        List<String> reasons = (List<String>) doc.get("warCause");
        if (reasons != null && !reasons.isEmpty()) {
            items.add(new SectionListItem("Lí do", reasons));
        }

        // MỤC TIÊU (object)
        Map<String, Object> object = (Map<String, Object>) doc.get("object");
        if (object != null) {
            List<String> usAllies = (List<String>) object.get("usAllies");
            List<String> vn = (List<String>) object.get("vn");
            if ((usAllies != null && !usAllies.isEmpty()) ||
                    (vn != null && !vn.isEmpty())) {
                items.add(new SectionListItem2("Mục tiêu", usAllies, vn));
            }
        }

        // VIDEO
        String youtubeId = doc.getString("youtubeId");
        if (TextUtils.isEmpty(youtubeId)) {
            Object raw = doc.get("videos");
            if (raw instanceof List) {
                for (Object o : (List<?>) raw) {
                    String link = null;
                    if (o instanceof String) {
                        link = (String) o;
                    } else if (o instanceof Map) {
                        Object l = ((Map<?, ?>) o).get("link");
                        if (l == null) l = ((Map<?, ?>) o).get("url");
                        if (l != null) link = String.valueOf(l);
                    }
                    String vid = YouTubeUtils.extractVideoId(link);
                    if (!TextUtils.isEmpty(vid)) {
                        youtubeId = vid;
                        Log.d("EventDetailActivity", "Found YouTube ID: " + youtubeId);
                        break;
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(youtubeId)) {
            items.add(new VideoItem("Tóm tắt diễn biến", youtubeId));
        }

        // KẾT QUẢ (result)
        Map<String, Object> content = (Map<String, Object>) doc.get("content");
        if (content != null) {
            Map<String, Object> result = (Map<String, Object>) content.get("result");
            if (result != null) {
                List<String> rUs = (List<String>) result.get("usAllies");
                List<String> rVn = (List<String>) result.get("vn");
                List<String> safeRUs = rUs != null ? rUs : new ArrayList<>();
                List<String> safeRVn = rVn != null ? rVn : new ArrayList<>();

                if (!safeRUs.isEmpty() || !safeRVn.isEmpty()) {
                    items.add(new SectionListItem2("Kết quả", safeRUs, safeRVn));
                }
            }
        }

        // Ý NGHĨA (meaning / impactOnPresent)
        List<String> meaning = (List<String>) doc.get("meaning");
        if (meaning == null) {
            String impact = doc.getString("impactOnPresent");
            if (!TextUtils.isEmpty(impact)) {
                meaning = new ArrayList<>();
                meaning.add(impact);
            }
        }
        if (meaning != null && !meaning.isEmpty()) {
            items.add(new SectionListItem("Ý nghĩa", meaning));
        }

        // Notify adapter sau khi populate xong
        adapter.notifyDataSetChanged();
        Log.d("EventDetailActivity", "UI items populated: " + items.size() + " items");
    }
}