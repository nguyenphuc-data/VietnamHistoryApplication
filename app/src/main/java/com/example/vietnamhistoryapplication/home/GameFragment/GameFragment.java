package com.example.vietnamhistoryapplication.home.GameFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.home.GameFragment.Quizz.QuizzFragment;
import com.example.vietnamhistoryapplication.home.GameFragment.TimeLinePuzzle.TimeLinePuzzleFragment;

public class GameFragment extends Fragment {

    private Button btnQuizz, btnTimeline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        btnQuizz = view.findViewById(R.id.btn_quizz);
        btnTimeline = view.findViewById(R.id.btn_timeline);

        // Mặc định mở QuizzFragment
        replaceFragment(new QuizzFragment());
        highlightButton(btnQuizz);

        btnQuizz.setOnClickListener(v -> {
            replaceFragment(new QuizzFragment());
            highlightButton(btnQuizz);
        });

        btnTimeline.setOnClickListener(v -> {
            replaceFragment(new TimeLinePuzzleFragment());
            highlightButton(btnTimeline);
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void highlightButton(Button selectedButton) {
        int normalColor = getResources().getColor(R.color.button_orange, null);
        int pressedColor = getResources().getColor(R.color.button_orange_dark, null);

        btnQuizz.setBackgroundTintList(android.content.res.ColorStateList.valueOf(normalColor));
        btnTimeline.setBackgroundTintList(android.content.res.ColorStateList.valueOf(normalColor));

        selectedButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pressedColor));
    }
}
