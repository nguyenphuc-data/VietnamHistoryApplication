package com.example.vietnamhistoryapplication.home.ExploreFragment;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.explore.article.AudioArticleListFragment;
import com.example.vietnamhistoryapplication.home.ExploreFragment.Explore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment implements ExploreAdapter.OnExploreClickListener {




    private ViewPager2 viewPager;
    private TextView tvTitle;
    private FloatingActionButton btnBack;
    private ExploreAdapter adapter;
    private final List<Explore> exploreItems = new ArrayList<>();
    public ExploreFragment() {
        // Required empty public constructor
    }
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.article_explore_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI
        tvTitle = view.findViewById(R.id.tvTitle1);
        viewPager = view.findViewById(R.id.viewPager2);

        // Title mặc định
        tvTitle.setText("Khám phá");

        // Setup ViewPager
        adapter = new ExploreAdapter(exploreItems, this);
        viewPager.setAdapter(adapter);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPadding(60, 0, 60, 0); // Tạo hiệu ứng peek

        // Load data
        loadExploreData();
    }

    @SuppressLint("RestrictedApi")
    private void loadExploreData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("explore")
                .orderBy("sortOrder", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    exploreItems.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String slug = doc.getId();
                        String title = doc.getString("title");
                        String description = doc.getString("description");
                        String coverMediaRef = doc.getString("coverMediaRef");
                        Long sortOrder = doc.getLong("sortOrder");

                        if (title != null && description != null) {
                            exploreItems.add(new Explore(
                                    slug,
                                    title,
                                    description,
                                    coverMediaRef,
                                    sortOrder != null ? sortOrder.intValue() : 999
                            ));
                        }
                    }

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + exploreItems.size() + " explore items");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading explore", e);
                    Toast.makeText(requireContext(), "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onExploreClick(Explore item) {
        if ("bai-bao".equals(item.getSlug())) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AudioArticleListFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}