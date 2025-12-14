package com.example.vietnamhistoryapplication.explore.articleDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.explore.articleDetail.Event;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticleFragment extends Fragment {

    private static final String TAG = "ArticleFragment";

    private ShapeableImageView imgCover;
    private TextView tvTitle, tvAuthors;
    private RecyclerView recyclerEvents;
    private ArticleAdapter adapter;
    private final List<Event> events = new ArrayList<>();

    private String documentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.article_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgCover = view.findViewById(R.id.imgCoverArticle);
        tvTitle = view.findViewById(R.id.tvTitleArticle);
        tvAuthors = view.findViewById(R.id.tvAuthors);
        recyclerEvents = view.findViewById(R.id.recyclerEvents);

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Setup RecyclerView
        recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ArticleAdapter(requireContext(), events);
        recyclerEvents.setAdapter(adapter);

        // Lấy documentId
        if (getArguments() != null) {
            documentId = getArguments().getString("documentId");
            if (documentId != null) {
                loadArticleFromFirestore(documentId);
            } else {
                Toast.makeText(requireContext(), "Không có ID bài báo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadArticleFromFirestore(String docId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("explore")
                .document("bai-bao")
                .collection("list")
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        parseAndDisplayArticle(documentSnapshot);
                    } else {
                        Toast.makeText(requireContext(), "Bài báo không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải bài báo", e);
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                });
    }

    private void parseAndDisplayArticle(DocumentSnapshot doc) {
        // Lấy dữ liệu cơ bản
        String title = doc.getString("title");
        String authors = doc.getString("authors");
        String coverMediaRef = doc.getString("coverMediaRef");

        tvTitle.setText(title != null ? title : "Không có tiêu đề");
        tvAuthors.setText(authors != null ? authors : "Không rõ nguồn");

        // Load ảnh bìa: DÙNG TRỰC TIẾP URL (không xử lý gs://)
        if (coverMediaRef != null && !coverMediaRef.isEmpty()) {
            Glide.with(this).load(coverMediaRef).placeholder(R.drawable.placeholder).into(imgCover);
        } else {
            imgCover.setImageResource(R.drawable.placeholder);
        }

        // Lấy danh sách diễn biến
        events.clear();
        Map<String, Object> content = (Map<String, Object>) doc.get("content");
        if (content != null) {
            Map<String, Object> dienBien = (Map<String, Object>) content.get("dienBien");
            if (dienBien != null) {
                List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) dienBien.get("events");
                if (eventMaps != null) {
                    for (Map<String, Object> eventMap : eventMaps) {
                        String details = (String) eventMap.get("details");
                        Map<String, Object> imageMap = (Map<String, Object>) eventMap.get("image");
                        if (imageMap != null) {
                            Event.ImageInfo image = new Event.ImageInfo();
                            image.content = (String) imageMap.get("content");
                            image.link = (String) imageMap.get("link"); // DÙNG TRỰC TIẾP

                            Event event = new Event();
                            event.details = details;
                            event.image = image;
                            events.add(event);
                        }
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}