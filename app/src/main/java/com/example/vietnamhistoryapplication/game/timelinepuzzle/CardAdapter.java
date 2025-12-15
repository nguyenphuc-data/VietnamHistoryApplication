package com.example.vietnamhistoryapplication.game.timelinepuzzle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.content.res.Resources;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.Event;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Event> eventList;
    private OnCardInteractionListener listener;

    public interface OnCardInteractionListener {
        void onCardClick(Event event, int position);
    }

    public CardAdapter(List<Event> eventList, OnCardInteractionListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvCardName.setText(event.getName());
        holder.tvCardDesc.setText(event.getDesc());

        Resources res = holder.itemView.getResources();

        final float LIFT_SLIGHTLY_PX = -res.getDisplayMetrics().density * 8;

        final float LIFT_HIGH_PX = -res.getDisplayMetrics().density * 16;


        holder.itemView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .translationY(LIFT_SLIGHTLY_PX)
                                .setDuration(80)
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        v.animate()
                                .translationY(LIFT_HIGH_PX)
                                .setDuration(60)
                                .start();

                        if (listener != null) {
                            int currentPosition = holder.getAdapterPosition();
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                listener.onCardClick(event, currentPosition);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .translationY(0f)
                                .setDuration(60)
                                .start();
                        return true;
                }
                return false;
            }
        });
    }


    public void animateFeedback(View view, boolean isCorrect) {
        if (isCorrect) {
            view.animate()
                    .translationY(0f)
                    .setDuration(60)
                    .start();
        } else {

            Resources res = view.getResources();
            final float SHAKE_DISTANCE_PX = res.getDisplayMetrics().density * 4;


            // Lắc sang trái (-4), sang phải (+4), lắc nhẹ trái (-2), về giữa (0)
            view.animate()
                    .translationX(-SHAKE_DISTANCE_PX)
                    .setDuration(15)
                    .withEndAction(() -> {
                        view.animate()
                                .translationX(SHAKE_DISTANCE_PX)
                                .setDuration(15)
                                .withEndAction(() -> {
                                    view.animate()
                                            .translationX(-SHAKE_DISTANCE_PX / 2)
                                            .setDuration(15)
                                            .withEndAction(() -> {
                                                view.animate()
                                                        .translationX(0f)
                                                        .translationY(0f)
                                                        .setDuration(15)
                                                        .start();
                                            })
                                            .start();
                                })
                                .start();
                    })
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardName;
        TextView tvCardDesc;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardName = itemView.findViewById(R.id.tvCardName);
            tvCardDesc = itemView.findViewById(R.id.tvCardDesc);
        }
    }
}