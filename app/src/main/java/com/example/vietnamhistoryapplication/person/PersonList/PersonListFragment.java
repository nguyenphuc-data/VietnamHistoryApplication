package com.example.vietnamhistoryapplication.person.PersonList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PersonListFragment extends Fragment {
    private RecyclerView recyclerView;
    private PersonListAdapter personListAdapter;
    private List<PersonListItem> personListList = new ArrayList<>();
    private String periodPersonSlug;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.person_list_activity, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewPersonList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ImageView ivBack = view.findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }


        if (getArguments() != null) {
            periodPersonSlug = getArguments().getString("periodSlug", "");
        } else {
            periodPersonSlug = "";
            Log.e("PersonListFragment", "periodPersonSlug is null, set to empty");
        }


        personListAdapter = new PersonListAdapter(personListList, periodPersonSlug);
        recyclerView.setAdapter(personListAdapter);


        if (!periodPersonSlug.isEmpty()) {
            Log.d("PersonListFragment", "Nhận periodPersonSlug: " + periodPersonSlug);
            loadPersonsFromFirestore(periodPersonSlug);
        } else {
            Log.e("PersonListFragment", "periodPersonSlug is empty");
        }

        return view;
    }

    private void loadPersonsFromFirestore(String periodPersonSlug) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String stageSlug= getArguments().getString("stageSlug","");
        String eventSlug= getArguments().getString("eventSlug","");
        db.collection("periods_person")
                .document(periodPersonSlug)
                .collection("persons")
                .orderBy("sortOrder")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    personListList.clear();
                    if (querySnapshot.isEmpty()) {
                        Log.d("PersonListFragment", "Không tìm thấy person nào cho periodPersonSlug: " + periodPersonSlug);
                    } else {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String personSlug = doc.getId();
                            boolean match = false;

                            db.collection("periods_person")
                                    .document(periodPersonSlug)
                                    .collection("persons")
                                    .document(personSlug)
                                    .collection("events")
                                    .get()
                                    .addOnSuccessListener(eventsSnapshot->{
                                        if(!eventsSnapshot.isEmpty()){
                                            for(QueryDocumentSnapshot doc_event: eventsSnapshot){
                                                String eventID = doc_event.getId();
                                                Log.d("Person","personSlug"+personSlug);
                                                Log.d("PersonListFragment","eventID: "+eventID+"eventSlug"+eventSlug);
                                                if(eventID.equals(eventSlug)){
                                                    String name = doc.getString("name");
                                                    String title = doc.getString("title");
                                                    String birthDate = doc.getString("birthDate");
                                                    String deathDate = doc.getString("deathDate");
                                                    String date = (birthDate != null ? birthDate : "") + " - " + (deathDate != null ? deathDate : "");
                                                    String image = doc.getString("coverMediaRef");
                                                    personListList.add(new PersonListItem(personSlug, name, date, title, image));
                                                    personListAdapter.notifyDataSetChanged();
                                                    Log.d("PersonListFragment", "Loaded " + personListList.size() + " persons, sorted by sortOrder");
                                                    break;
                                                }

                                            }
                                        }
                                    });

                        }

                    }

                })
                .addOnFailureListener(e -> Log.e("PersonListFragment", "Lỗi Firestore: " + e.getMessage()));
    }
}
