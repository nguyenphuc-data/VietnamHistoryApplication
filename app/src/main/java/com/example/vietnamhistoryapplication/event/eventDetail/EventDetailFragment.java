package com.example.vietnamhistoryapplication.event.eventDetail;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.YouTubeUtils;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.*;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailFragment extends Fragment {

    private static final String ARG_PERIOD = "periodSlug";
    private static final String ARG_STAGE  = "stageSlug";
    private static final String ARG_EVENT  = "eventSlug";

    private final ArrayList<EventDetailItem> items = new ArrayList<>();
    private EventDetailAdapter adapter;

    // UI
    private ImageView ivBack;
    private TextView  tvTitle;
    private RecyclerView rv;

    // args
    private String periodSlug, stageSlug, eventSlug;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail_fragment, container, false);
        rv = view.findViewById(R.id.recycler_view_event_details);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));  // Đảm bảo LayoutManager đã được gắn vào RecyclerView

        adapter = new EventDetailAdapter(items, getViewLifecycleOwner());  // Gắn adapter đúng cách
        rv.setAdapter(adapter);  // Gắn adapter vào RecyclerView

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy view
        ivBack = view.findViewById(R.id.ivBack);
        tvTitle = view.findViewById(R.id.tvTitle);
        rv = view.findViewById(R.id.recycler_view_event_details);

        // Xử lý sự kiện back
        ivBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        // Lấy tham số từ bundle
        Bundle args = getArguments();
//        if (args == null) {
//            Toast.makeText(requireContext(), "Thiếu tham số sự kiện.", Toast.LENGTH_LONG).show();
//            requireActivity().getOnBackPressedDispatcher().onBackPressed();
//            return;
//        }
        periodSlug = args.getString(ARG_PERIOD);
        stageSlug = args.getString(ARG_STAGE);
        eventSlug = args.getString(ARG_EVENT);

        Log.d("EventDetailFragment", "periodSlug=" + periodSlug + ", stageSlug=" + stageSlug + ", eventSlug=" + eventSlug);

        if (TextUtils.isEmpty(periodSlug) || TextUtils.isEmpty(stageSlug) || TextUtils.isEmpty(eventSlug)) {
            Toast.makeText(requireContext(), "Thiếu tham số event (period/stage/event).", Toast.LENGTH_LONG).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }

        // Cài đặt RecyclerView
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new EventDetailAdapter(items, (LifecycleOwner) this);
//        rv.setAdapter(adapter);

        // Tải dữ liệu từ Firestore
        loadFromFirestore(periodSlug, stageSlug, eventSlug);
    }

    private void loadFromFirestore(String periodSlug, String stageSlug, String eventSlug) {
        FirebaseFirestore.getInstance()
                .collection("periods").document(periodSlug)
                .collection("stages").document(stageSlug)
                .collection("events").document(eventSlug)
                .get()
                .addOnSuccessListener(this::mapDocToUi)
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Không tải được dữ liệu.", Toast.LENGTH_LONG).show()
                );
    }

    @SuppressWarnings("unchecked")
    private void mapDocToUi(DocumentSnapshot doc) {
        items.clear();

        String title = doc.getString("title");
        String smallTitle = doc.getString("smallTitle");
//        TextView tvTitle = findViewById(R.id.tvTitle);  // Lấy tvTitle từ layout
//        tvTitle.setText(title != null ? title : "Tiêu đề không có");

        // HEADER
        List<Map<String,Object>> images = (List<Map<String,Object>>) doc.get("images");
        String cover = doc.getString("coverMediaRef");
        if (cover == null && images != null && !images.isEmpty()) {
            Object l = images.get(0).get("link");
            if (l != null) cover = String.valueOf(l);
        }
        items.add(new HeaderItem(
                TextUtils.isEmpty(title) ? "" : title,
                TextUtils.isEmpty(smallTitle) ? "" : smallTitle,
                cover
        ));

        // INTRO (summary + ảnh tài liệu)
        String overview = doc.getString("summary");
        String docImage = (images != null && !images.isEmpty())
                ? String.valueOf(images.get(3).get("link")) : cover;
        items.add(new IntroItem(
                TextUtils.isEmpty(overview) ? "" : overview,
                docImage
        ));

        // LÍ DO (warCause)
        List<String> reasons = (List<String>) doc.get("warCause");
        if (reasons != null && !reasons.isEmpty()) items.add(new SectionListItem("Lí do", reasons));

        // MỤC TIÊU (usAllies & vn)
        Map<String,Object> object = (Map<String,Object>) doc.get("object");
        if (object != null) {
            List<String> usAllies = (List<String>) object.get("usAllies");
            List<String> vn       = (List<String>) object.get("vn");
            if ((usAllies != null && !usAllies.isEmpty()) ||
                    (vn != null && !vn.isEmpty())) {
                // Title truyền từ Activity (không fix cứng trong XML)
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
                    if (o instanceof String) link = (String) o;
                    else if (o instanceof Map) {
                        Object l = ((Map<?,?>) o).get("link");
                        if (l == null) l = ((Map<?,?>) o).get("url");
                        if (l != null) link = String.valueOf(l);
                    }
                    String vid = YouTubeUtils.extractVideoId(link);
                    if (!TextUtils.isEmpty(vid)) { youtubeId = vid; break; }
                }
            }
        }
        if (!TextUtils.isEmpty(youtubeId)) {
            items.add(new VideoItem("Tóm tắt diễn biến", youtubeId));
        }

        // SLIDES (ảnh nền + tiêu đề + nội dung)
//        if (images != null && !images.isEmpty()) {
//            items.add(new SlidesItem(images, "Nội dung"));
//        }

        Map<String, Object> content = (Map<String, Object>) doc.get("content");
        if (content != null) {
            List<Map<String, Object>> warSummary = (List<Map<String, Object>>) content.get("warSummary");
            if (warSummary != null && !warSummary.isEmpty()) {
                List<Map<String, Object>> slideItems = new ArrayList<>();  // Tạo danh sách slides

                for (int i = 0; i < warSummary.size(); i++) {
                    Map<String, Object> slideData = warSummary.get(i);
                    String detail = (String) slideData.get("detail"); // Lấy chi tiết của slide
                    List<Map<String, Object>> slideImages = (List<Map<String, Object>>) slideData.get("images");

                    String slideImageLink = null;
                    // Lấy ảnh đầu tiên nếu có
                    if (slideImages != null && !slideImages.isEmpty()) {
                        slideImageLink = (String) slideImages.get(0).get("link");
                    }

                    // Tạo một Map cho mỗi slide và thêm vào danh sách
                    Map<String, Object> slide = new HashMap<>();
                    slide.put("link", slideImageLink);
                    slide.put("detail", detail);

                    slideItems.add(slide);  // Thêm vào danh sách slide
                }

                // Thêm danh sách slide vào items
                items.add(new SlidesItem(slideItems, "Nội dung"));
                Log.d("EventDetailFragment", "warSummary: " + warSummary.toString());
                Log.d("EventDetailFragment", "slideItems: " + slideItems.toString());

            }
        }

        // KẾT QUẢ (result)
        Map<String,Object> content1 = (Map<String, Object>) doc.get("content");
        if (content1 != null) {
            Map<String,Object> result = (Map<String, Object>) content1.get("result");
            if (result != null) {
                List<String> rUs = (List<String>) result.get("usAllies");
                List<String> rVn = (List<String>) result.get("vn");
                if ((rUs != null && !rUs.isEmpty()) || (rVn != null && !rVn.isEmpty())) {
                    items.add(new SectionListItem2("Kết quả",
                            rUs == null ? new ArrayList<>() : rUs,
                            rVn == null ? new ArrayList<>() : rVn));
                }
            }
        }

        // Ý NGHĨA (meaning / impactOnPresent)
        List<String> meaning = (List<String>) doc.get("meaning");
        if (meaning == null) {
            String impact = doc.getString("impactOnPresent");
            if (!TextUtils.isEmpty(impact)) {
                meaning = new ArrayList<>(); meaning.add(impact);
            }
        }
        if (meaning != null && !meaning.isEmpty()) items.add(new SectionListItem("Ý nghĩa", meaning));

        adapter.notifyDataSetChanged();
    }
}
