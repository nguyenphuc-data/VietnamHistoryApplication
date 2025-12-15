package com.example.vietnamhistoryapplication.home.GameFragment.TimeLinePuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.game.timelinepuzzle.TimeLinePuzzleDetail;
import com.example.vietnamhistoryapplication.models.Era;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TimeLinePuzzleFragment extends Fragment {

    private RecyclerView recyclerEras;
    private EraListAdapter adapter;
    private List<Era> eraList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_line_puzzle_fragment, container, false);

        recyclerEras = view.findViewById(R.id.recyclerEras);
        recyclerEras.setLayoutManager(new LinearLayoutManager(getContext()));

        eraList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        loadErasFromFirestore();
        adapter = new EraListAdapter(eraList, era -> {
            Intent intent = new Intent(getActivity(), TimeLinePuzzleDetail.class);
            intent.putExtra("era", era);
            startActivity(intent);
        });
        recyclerEras.setAdapter(adapter);
        return view;

    }

    private void loadErasFromFirestore() {
        db.collection("games")
                .document("timelinepuzzle")
                .collection("eras")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eraList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Era era = document.toObject(Era.class);
                            era.setEraId(document.getId());
                            eraList.add(era);
                        }

                    adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}