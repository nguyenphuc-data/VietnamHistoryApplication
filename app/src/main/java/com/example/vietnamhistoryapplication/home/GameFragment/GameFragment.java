package com.example.vietnamhistoryapplication.home.GameFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.game.quizzes.QuizzesDetail;
import com.example.vietnamhistoryapplication.models.GameItem;
import com.example.vietnamhistoryapplication.models.QuizzItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class GameFragment extends Fragment {


    public List<QuizzItem> quizzItems = new ArrayList<>();
    GameAdapter gameAdapter;
    public GameFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.game_fragment, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerGames);

        loadGameFromFirestore();
        gameAdapter = new GameAdapter(quizzItems, this::startQuizzDetail);
        recyclerView.setAdapter(gameAdapter);
    }
    private void loadGameFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("games")
                .document("quiz-lich-su-viet-nam")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        String type = documentSnapshot.getString("type");
                        db.collection("games")
                                .document("quiz-lich-su-viet-nam")
                                .collection("quizzes")
                                .get()
                                .addOnSuccessListener(querySnapshot ->{
                                    if(!querySnapshot.isEmpty()){
                                        for(QueryDocumentSnapshot doc : querySnapshot){
                                            String quizzslug = doc.getId();
                                            String level = doc.getString("level");
                                            Map<String, String> eventId = (Map<String, String>) doc.get("eventID");
                                            Map<String, Long> settings = (Map<String, Long>) doc.get("settings");
                                            String description = doc.getString("description");
                                            Integer questionCount = doc.getLong("questionCount").intValue();
                                            QuizzItem quizzItem = new QuizzItem(quizzslug,level,eventId,settings,description,type,questionCount);
                                            quizzItems.add(quizzItem);
                                            Log.d("GameFragment","Loaded quizz: "+quizzslug);
                                            gameAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } );
                        Log.d("GameFragment","Loaded game: "+type);
                        }else{
                        Log.d("GameFragment","Game not found");

                    }
                });
    }
    private void startQuizzDetail(QuizzItem quizzItem) {
        Intent intent = new Intent(getActivity(), QuizzesDetail.class);
        intent.putExtra("quizzItem", quizzItem);
        startActivity(intent);
    }
}