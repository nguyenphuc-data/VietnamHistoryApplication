package com.example.vietnamhistoryapplication.person.PersonList;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vietnamhistoryapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PersonListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PersonListAdapter personListAdapter;
    private List<PersonListItem> personListList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_list_activity);

        // Gắn sự kiện quay về cho nút back
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> onBackPressed());
        }

        recyclerView = findViewById(R.id.recyclerViewPersonList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String periodPersonSlug = getIntent().getStringExtra("period_slug");
        if (periodPersonSlug == null) {
            periodPersonSlug = "";
            Log.e("PersonListActivity", "periodPersonSlug is null, set to empty");
        }

        personListAdapter = new PersonListAdapter(personListList, periodPersonSlug);
        recyclerView.setAdapter(personListAdapter);

        if (!periodPersonSlug.isEmpty()) {
            Log.d("PersonListActivity", "Nhận periodPersonSlug: " + periodPersonSlug);
            loadPersonsFromFirestore(periodPersonSlug);
        } else {
            Log.e("PersonListActivity", "periodPersonSlug is empty");
        }
    }

    private void loadPersonsFromFirestore(String periodPersonSlug) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("periods_person")
                .document(periodPersonSlug)
                .collection("persons")
                .orderBy("sortOrder")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    personListList.clear();
                    if (querySnapshot.isEmpty()) {
                        Log.d("PersonListActivity", "Không tìm thấy person nào cho periodPersonSlug: " + periodPersonSlug);
                    } else {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String personSlug = doc.getId();
                            String name = doc.getString("name");
                            String title = doc.getString("title");
                            String birthDate = doc.getString("birthDate");
                            String deathDate = doc.getString("deathDate");
                            String date = birthDate + " - " + deathDate;
                            String image = doc.getString("coverMediaRef");
                            personListList.add(new PersonListItem(personSlug, name, date, title, image));
                        }
                        Log.d("PersonListActivity", "Loaded " + personListList.size() + " persons, sorted by sortOrder");
                    }
                    personListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonListActivity", "Lỗi Firestore: " + e.getMessage());
                });
    }

}