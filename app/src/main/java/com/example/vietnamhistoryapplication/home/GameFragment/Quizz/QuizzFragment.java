package com.example.vietnamhistoryapplication.home.GameFragment.Quizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.game.quizzes.QuizzesDetail;
import com.example.vietnamhistoryapplication.models.QuizzItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizzFragment extends  Fragment{
    public List<QuizzItem> quizzItems = new ArrayList<>();
    QuizzAdapter quizzAdapter;
    public  QuizzFragment(){
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.quizz_fragment, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerGames);

        loadGameFromFirestore();
        quizzAdapter = new QuizzAdapter(quizzItems, this::startQuizzDetail);
        recyclerView.setAdapter(quizzAdapter);
    }
    private void loadGameFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy argument truyền vào (có thể null)
        Bundle args = getArguments();
        String periodSlug = args != null ? args.getString("periodSlug") : null;
        String stageSlug = args != null ? args.getString("stageSlug") : null;
        String eventSlug = args != null ? args.getString("eventSlug") : null;

        db.collection("games")
                .document("quiz-lich-su-viet-nam")
                .collection("quizzes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    quizzItems.clear();

                    if (!querySnapshot.isEmpty()) {
                        // Lấy tất
                        if ((periodSlug == null && stageSlug == null && eventSlug == null)) {
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                String quizzslug = doc.getId();
                                String level = doc.getString("level");
                                Map<String, String> eventId = (Map<String, String>) doc.get("eventID");
                                Map<String, Long> settings = (Map<String, Long>) doc.get("settings");
                                String description = doc.getString("description");
                                String type = "quizzes";
                                Integer questionCount = doc.getLong("questionCount").intValue();

                                QuizzItem quizzItem = new QuizzItem(
                                        quizzslug, level, eventId, settings, description, type, questionCount
                                );
                                quizzItems.add(quizzItem);
                            }
                        }
                        else{
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Map<String, String> eventId = (Map<String, String>) doc.get("eventID");
                                if (eventId == null) continue;

                                // Nếu có slug thì chỉ thêm quiz phù hợp
                                boolean match = true;
                                if (!periodSlug.equals(eventId.get("periodID"))) match = false;
                                if (!stageSlug.equals(eventId.get("stageID"))) match = false;
                                if (!eventSlug.equals(eventId.get("eventid"))) match = false;

                                if (match) {
                                    String quizzslug = doc.getId();
                                    String level = doc.getString("level");
                                    Map<String, Long> settings = (Map<String, Long>) doc.get("settings");
                                    String description = doc.getString("description");
                                    String type = "quizzes";
                                    Integer questionCount = doc.getLong("questionCount").intValue();

                                    QuizzItem quizzItem = new QuizzItem(
                                            quizzslug, level, eventId, settings, description, type, questionCount
                                    );
                                    quizzItems.add(quizzItem);
                                    Log.d("GameFragment", "Loaded quizz: " + quizzslug);
                                }
                            }
                        }




                        quizzAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("GameFragment", "Error loading quizzes", e));
    }
    private void startQuizzDetail(QuizzItem quizzItem) {
        Intent intent = new Intent(getActivity(), QuizzesDetail.class);
        intent.putExtra("quizzItem", quizzItem);
        startActivity(intent);
    }
}
