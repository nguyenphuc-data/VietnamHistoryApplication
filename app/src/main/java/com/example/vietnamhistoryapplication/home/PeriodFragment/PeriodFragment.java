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
import com.example.vietnamhistoryapplication.period.PeriodDetailFragment;
import com.example.vietnamhistoryapplication.stage.StageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        viewPager = view.findViewById(R.id.viewPager);
        loadPeriodsFromFirestore();
        periodAdapter = new PeriodAdapter(periodList, this::startStageActivity);
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
                                String slug = document.getId(); // lấy slug từ document ID
                                String title = document.getString("title");
                                Timestamp startTimestamp = document.getTimestamp("startDate");
                                Timestamp endTimestamp = document.getTimestamp("endDate");
                                String summary = document.getString("summary");
                                String image = document.getString("coverMediaRef");
                                String description = document.getString("description");
                                String startYear = startTimestamp != null ? extractYearFromTimestamp(startTimestamp) : "N/A";
                                String endYear = endTimestamp != null ? extractYearFromTimestamp(endTimestamp) : "N/A";
                                String periodRange = startYear + "–" + endYear;

                                if (title != null) {
                                    periodList.add(new Period(slug, title, periodRange, summary,image,description));
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

    // chuyển sang StageActivity (không phải PeriodDetailActivity)
    private void startStageActivity(Period period) {
//        Intent intent = new Intent(getActivity(), StageActivity.class);
//        intent.putExtra("slug", slug); // gửi slug sang StageActivity
//        startActivity(intent);
        Fragment fragment = PeriodDetailFragment.newInstance(period);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }



    // Adapter cho ViewPager2

}
