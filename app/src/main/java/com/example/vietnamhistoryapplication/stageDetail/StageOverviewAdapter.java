package com.example.vietnamhistoryapplication.stageDetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.home.PeriodFragment.Period;
import com.example.vietnamhistoryapplication.home.PeriodFragment.PeriodAdapter;
import android.view.View;
import android.view.ViewGroup;

public class StageOverviewAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<StageOverviewAdapter.ViewHolder>{
    private StageDetailItem stageDetailItem;
    public StageOverviewAdapter(StageDetailItem stageDetailItem) {
        this.stageDetailItem = stageDetailItem;

    }
    public StageOverviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stage_detail_overview_item, parent, false);
        return new StageOverviewAdapter.ViewHolder(view);
    }
    public void onBindViewHolder(@NonNull StageOverviewAdapter.ViewHolder holder, int position) {
        if(position==0){
            holder.tvStageOverview.setText("tổng quan");
            holder.tvStageOverviewDetail.setText(stageDetailItem.overview);

        }else if(position==1){
            holder.tvStageOverview.setText("kết quả");
            StringBuilder sb = new StringBuilder();
            for (String item : stageDetailItem.result) {
                sb.append("• ").append(item).append("\n");
            }
            holder.tvStageOverviewDetail.setText(sb.toString());
        }else if(position==2){
            holder.tvStageOverview.setText("Ảnh hưởng");
            holder.tvStageOverviewDetail.setText(stageDetailItem.impactOnPresent);

        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStageOverview,tvStageOverviewDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStageOverview = itemView.findViewById(R.id.tvStageOverview);
            tvStageOverviewDetail = itemView.findViewById(R.id.tvStageOverviewDetail);

        }
    }
}
