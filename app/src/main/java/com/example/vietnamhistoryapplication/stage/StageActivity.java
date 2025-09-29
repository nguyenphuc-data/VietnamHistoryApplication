package com.example.vietnamhistoryapplication.stage;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vietnamhistoryapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StageAdapter stageAdapter;
    private List<StageItem> stageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stages_activity);

        recyclerView = findViewById(R.id.recyclerViewStages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stageAdapter = new StageAdapter(stageList);
        recyclerView.setAdapter(stageAdapter);

        String periodSlug = getIntent().getStringExtra("slug");
        if (periodSlug != null) {
            Log.d("StageActivity", "Nhận periodSlug: " + periodSlug);
            loadStagesFromFirestore(periodSlug);
        } else {
            Log.e("StageActivity", "periodSlug is null");
        }
    }

    private void loadStagesFromFirestore(String periodSlug) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("periods")
                .document(periodSlug)
                .collection("stages")
                .orderBy("sortOrder")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    stageList.clear();
                    if (querySnapshot.isEmpty()) {
                        Log.d("StageActivity", "Không tìm thấy stages cho periodSlug: " + periodSlug);
                    } else {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String stageSlug = doc.getId();
                            String title = doc.getString("title");
                            Timestamp startDate = doc.getTimestamp("startDate");
                            Timestamp endDate = doc.getTimestamp("endDate");
                            String stageRange = formatDateRange(startDate, endDate);
                            String overview = doc.getString("overview");
                            stageList.add(new StageItem(stageSlug, title, stageRange, overview));
                        }
                        Log.d("StageActivity", "Loaded " + stageList.size() + " stages, sorted by sortOrder");
                    }
                    stageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("StageActivity", "Lỗi Firestore: " + e.getMessage());
                });
    }

    private String formatDateRange(Timestamp start, Timestamp end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        String startYear = start != null ? sdf.format(start.toDate()) : "N/A";
        String endYear = end != null ? sdf.format(end.toDate()) : "N/A";
        return startYear + "–" + endYear;
    }
}