package com.example.vietnamhistoryapplication.home.PersonFragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.person.PersonList.PersonListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PersonPeriodFragment extends Fragment {
    private RecyclerView recyclerView;
    private PersonPeriodAdapter personPeriodAdapter;
    private List<PersonPeriodItem> personPeriodList = new ArrayList<>();
    private TextView tvNoPeriod;

    public PersonPeriodFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_period_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewPersonPeriod);
        tvNoPeriod = view.findViewById(R.id.tvNoPeriod);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personPeriodAdapter = new PersonPeriodAdapter(personPeriodList, this::startPersonListActivity);
        recyclerView.setAdapter(personPeriodAdapter);
        loadPersonPeriodFromFirestore();
    }

    private void startPersonListActivity(String slug) {
        Intent intent = new Intent(getContext(), PersonListActivity.class);
        intent.putExtra("period_slug", slug);
        startActivity(intent);
    }

    private void loadPersonPeriodFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("periods_person")
                .orderBy("sortOrder")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            personPeriodList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String slug = document.getId(); // lấy slug từ document ID
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
                        } else {
                            Log.e("PersonPeriodFragment", "Error loading data", task.getException());
                            updateEmptyPeriod();
                        }
                    }
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

    private String extractYearFromTimestamp(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp.toDate());
        return String.valueOf(cal.get(Calendar.YEAR));
    }
    private String formatDateRange(Timestamp start, Timestamp end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        String startYear = start != null ? sdf.format(start.toDate()) : "N/A";
        String endYear = end != null ? sdf.format(end.toDate()) : "N/A";
        return startYear + "–" + endYear;
    }
}