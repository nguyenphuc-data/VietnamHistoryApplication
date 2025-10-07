package com.example.vietnamhistoryapplication.home.PersonFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PersonPeriodFragment extends Fragment {
    private RecyclerView recyclerView;
    private PersonPeriodAdapter personPeriodAdapter;
    private List<PersonPeriodItem> personPeriodList = new ArrayList<>();
    private TextView tvNoPeriod;

    public PersonPeriodFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_period_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewPersonPeriod);
        tvNoPeriod = view.findViewById(R.id.tvNoPeriod);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personPeriodAdapter = new PersonPeriodAdapter(personPeriodList);
        recyclerView.setAdapter(personPeriodAdapter);

        loadPersonPeriodFromFirestore();
    }

    private void loadPersonPeriodFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("periods_person")
                .orderBy("sortOrder")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    personPeriodList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String slug = document.getId();
                        String title = document.getString("title");
                        Timestamp startDate = document.getTimestamp("startDate");
                        Timestamp endDate = document.getTimestamp("endDate");
                        String image = document.getString("coverMediaRef");
                        String periodRange = formatDateRange(startDate, endDate);

                        if (title != null) {
                            personPeriodList.add(new PersonPeriodItem(slug, title, periodRange, image));
                        }
                    }
                    Log.d("PersonPeriodFragment", "Loaded " + personPeriodList.size() + " person periods");
                    personPeriodAdapter.notifyDataSetChanged();
                    updateEmptyPeriod();
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonPeriodFragment", "Error loading data: " + e.getMessage());
                    updateEmptyPeriod();
                });
    }

    private void updateEmptyPeriod() {
        if (personPeriodList.isEmpty()) {
            tvNoPeriod.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoPeriod.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private String formatDateRange(Timestamp start, Timestamp end) {
        String startYear = "truyền thuyết";
        if (start != null) {
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(start.toDate());
            startYear = String.valueOf(calStart.get(Calendar.YEAR));
        }

        String endYear = "nay";
        if (end != null) {
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(end.toDate());
            endYear = String.valueOf(calEnd.get(Calendar.YEAR));
        }

        return startYear + "–" + endYear;
    }
}
