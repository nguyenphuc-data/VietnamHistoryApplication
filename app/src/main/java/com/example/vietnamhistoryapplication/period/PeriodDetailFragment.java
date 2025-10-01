package com.example.vietnamhistoryapplication.period;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.home.PeriodFragment.Period;
import com.example.vietnamhistoryapplication.stage.StageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class PeriodDetailFragment extends Fragment {
    private static final String ARG_PERIOD = "arg_period";
    private Period period;
    public  PeriodDetailFragment() {

    }

    public static PeriodDetailFragment newInstance(Period period){
        PeriodDetailFragment fragment = new PeriodDetailFragment();
        Bundle arges = new Bundle();
        arges.putSerializable("period",period);
        fragment.setArguments(arges);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            period = (Period) getArguments().getSerializable("period");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.period_detail_fragment, container, false);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDescriptoin = view.findViewById(R.id.tvDescription);
        ImageView ivImage = view.findViewById(R.id.ivImage);
        Button btnReadMore = view.findViewById(R.id.btnReadMore);

        tvTitle.setText(period.getTitle());
        tvDescriptoin.setText(period.getDescription());
        //load ảnh
        ImageLoader.loadImage(ivImage, period.getImage());

        btnReadMore.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), StageActivity.class);
            intent.putExtra("slug", period.getSlug());
            startActivity(intent);
        });

        FloatingActionButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}