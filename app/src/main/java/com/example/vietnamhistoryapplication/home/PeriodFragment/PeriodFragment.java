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
    private PeriodAdapter periodAdapter;
    private List<Period> periodList = new ArrayList<>();

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
        loadPeriodsFromFirestore();
        viewPager = view.findViewById(R.id.viewPager);
        periodAdapter = new PeriodAdapter(periodList, this::startPeriodActivity);
        viewPager.setAdapter(periodAdapter);
    }

    private void loadPeriodsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("periods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            periodList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String slug = document.getId();
                                String title = document.getString("title");
                                Timestamp startTimestamp = document.getTimestamp("startDate");
                                Timestamp endTimestamp = document.getTimestamp("endDate");
                                String description = document.getString("description");

                                String startYear = startTimestamp != null ? extractYearFromTimestamp(startTimestamp) : "N/A";
                                String endYear = endTimestamp != null ? extractYearFromTimestamp(endTimestamp) : "N/A";
                                String periodRange = startYear + "–" + endYear;

                                if (title != null) {
                                    periodList.add(new Period(slug, title, periodRange, description));
                                }
                            }
                            Log.d("PeriodFragment", "Loaded " + periodList.size() + " periods");
                            periodAdapter.notifyDataSetChanged();
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

    private void startPeriodActivity(String slug) {
        Intent intent = new Intent(getActivity(), PeriodDetailActivity.class);
        intent.putExtra("period_slug", slug);
        startActivity(intent);
    }

    // Model class cho Period
    public static class Period {
        String slug, title, periodRange, description;

        public Period(String slug, String title, String periodRange, String description) {
            this.slug = slug;
            this.title = title;
            this.periodRange = periodRange;
            this.description = description;
        }
    }

    // Adapter cho ViewPager2
    public static class PeriodAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<PeriodAdapter.ViewHolder> {
        private List<Period> periods;
        private final OnPeriodClickListener clickListener;

        public interface OnPeriodClickListener {
            void onPeriodClick(String slug);
        }

        public PeriodAdapter(List<Period> periods, OnPeriodClickListener clickListener) {
            this.periods = periods;
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
            Period period = periods.get(position);
            holder.tvTitle.setText(period.title != null ? period.title : "No Title");
            holder.tvPeriod.setText(period.periodRange != null ? period.periodRange : "No Period");
            holder.tvDescription.setText(period.description != null ? period.description : "No Description");

            holder.itemView.setOnClickListener(v -> clickListener.onPeriodClick(period.slug));
        }

        @Override
        public int getItemCount() {
            return periods.size();
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
