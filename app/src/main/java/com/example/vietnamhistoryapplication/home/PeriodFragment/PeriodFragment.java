package com.example.vietnamhistoryapplication.home.PeriodFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.period.PeriodDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PeriodFragment extends Fragment {

    private ViewPager2 viewPager;
    private ThemeAdapter themeAdapter;
    private List<Theme> themeList = new ArrayList<>();

    public PeriodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.period_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadThemesFromFirestore();
        viewPager = view.findViewById(R.id.viewPager);
        themeAdapter = new ThemeAdapter(themeList, this::startThemeActivity);
        viewPager.setAdapter(themeAdapter);
    }

    private void loadThemesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("themes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            themeList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String slug = document.getId();
                                String title = document.getString("title");
                                Timestamp startTimestamp = document.getTimestamp("startDate");
                                Timestamp endTimestamp = document.getTimestamp("endDate");
                                String description = document.getString("description");

                                // Trích xuất năm từ Timestamp
                                String startYear = startTimestamp != null ? extractYearFromTimestamp(startTimestamp) : "N/A";
                                String endYear = endTimestamp != null ? extractYearFromTimestamp(endTimestamp) : "N/A";
                                String period = startYear + "–" + endYear;

                                // Thêm vào list nếu title không null
                                if (title != null) {
                                    themeList.add(new Theme(slug, title, period, description));
                                }
                            }
                            Log.d("PeriodFragment", "Loaded " + themeList.size() + " themes");
                            themeAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("PeriodFragment", "Error loading data", task.getException());
                        }
                    }
                });
    }

    private String extractYearFromTimestamp(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp.toDate());
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    private void startThemeActivity(String slug) {
        Intent intent = new Intent(getActivity(), PeriodDetailActivity.class);
        intent.putExtra("theme_slug", slug);
        startActivity(intent);
    }

    // Model class cho Theme
    public static class Theme {
        String slug, title, period, description;

        public Theme(String slug, String title, String period, String description) {
            this.slug = slug;
            this.title = title;
            this.period = period;
            this.description = description;
        }
    }

    // Adapter cho ViewPager2
    public static class ThemeAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ThemeAdapter.ViewHolder> {
        private List<Theme> themes;
        private final OnThemeClickListener clickListener;

        public interface OnThemeClickListener {
            void onThemeClick(String slug);
        }

        public ThemeAdapter(List<Theme> themes, OnThemeClickListener clickListener) {
            this.themes = themes;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.periods_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Theme theme = themes.get(position);
            holder.tvTitle.setText(theme.title != null ? theme.title : "No Title");
            holder.tvPeriod.setText(theme.period != null ? theme.period : "No Period");
            holder.tvDescription.setText(theme.description != null ? theme.description : "No Description");

            // Thêm sự kiện nhấn vào item
            holder.itemView.setOnClickListener(v -> clickListener.onThemeClick(theme.slug));
        }

        @Override
        public int getItemCount() {
            return themes.size();
        }

        public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView tvTitle, tvPeriod, tvDescription;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPeriod = itemView.findViewById(R.id.tvPeriod);
                tvDescription = itemView.findViewById(R.id.tvDescription);
            }
        }
    }
}