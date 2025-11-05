// com.example.vietnamhistoryapplication/explore/audio/AudioArticleListFragment.java
package com.example.vietnamhistoryapplication.explore.article;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.vietnamhistoryapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AudioArticleListFragment extends Fragment {

    private static final String TAG = "AudioArticleList";

    private RecyclerView recyclerView;
    private AudioArticleAdapter adapter;
    private final List<AudioArticle> articles = new ArrayList<>();
    private final List<AudioArticle> filteredArticles = new ArrayList<>(); // Danh sách lọc
    private TextInputEditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.article_fagment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button
        FloatingActionButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Title
        TextView tvTitle = view.findViewById(R.id.tvTitle);
//        tvTitle.setText("Báo và Audio");

        // Search Input
        etSearch = view.findViewById(R.id.etSearch);

        // RecyclerView
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AudioArticleAdapter(filteredArticles); // Dùng filtered
        recyclerView.setAdapter(adapter);

        // Load data
        loadArticlesFromFirestore();

        // TÌM KIẾM THEO TIÊU ĐỀ
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArticles(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadArticlesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("explore")
                .document("bai-bao")
                .collection("list")
                .orderBy("sortOrder")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    articles.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String title = doc.getString("title");
                        String excerpt = doc.getString("excerpt");
                        String thumbMediaRef = doc.getString("thumbMediaRef");

                        // LẤY audioUrl AN TOÀN
                        String audioUrl = null;
                        Map<String, Object> data = doc.getData();
                        if (data != null && data.containsKey("audio")) {
                            Object audioObj = data.get("audio");
                            if (audioObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> audioMap = (Map<String, Object>) audioObj;

                                if (audioMap.containsKey("tracks")) {
                                    Object tracksObj = audioMap.get("tracks");
                                    if (tracksObj instanceof Map) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> tracksMap = (Map<String, Object>) tracksObj;
                                        audioUrl = (String) tracksMap.get("mediaRef");
                                    }
                                }
                            }
                        }

                        String rawAudioUrl = convertToGitHubRawUrl(audioUrl);
                        if (title != null && rawAudioUrl != null) {
                            AudioArticle article = new AudioArticle(id, title, excerpt, thumbMediaRef, rawAudioUrl);
                            articles.add(article);
                        }
                    }

                    // Cập nhật danh sách lọc = toàn bộ
                    filteredArticles.clear();
                    filteredArticles.addAll(articles);
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + articles.size() + " audio articles");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading articles", e);
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                });
    }

    // HÀM LỌC THEO TÌM KIẾM
    private void filterArticles(String query) {
        filteredArticles.clear();
        if (query.isEmpty()) {
            filteredArticles.addAll(articles);
        } else {
            String lowerQuery = query.toLowerCase();
            for (AudioArticle article : articles) {
                if (article.getTitle().toLowerCase().contains(lowerQuery)) {
                    filteredArticles.add(article);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // CHUYỂN URL GITHUB BLOB → RAW
    private String convertToGitHubRawUrl(String blobUrl) {
        if (blobUrl == null || !blobUrl.contains("github.com") || !blobUrl.contains("/blob/")) {
            return blobUrl;
        }
        return blobUrl
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/blob/", "/");
    }
}