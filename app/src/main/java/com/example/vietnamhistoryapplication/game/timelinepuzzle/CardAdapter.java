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
        // BIẾN 'event' ĐẠI DIỆN CHO OBJECT EVENT MODEL
        Event event = eventList.get(position);

        holder.tvCardName.setText(event.getName());
        holder.tvCardDesc.setText(event.getDesc());

        // Chuyển đổi dp sang px
        Resources res = holder.itemView.getResources();
        // Nâng nhẹ khi chạm (8dp)
        final float LIFT_SLIGHTLY_PX = -res.getDisplayMetrics().density * 8;
        // Nâng hẳn lên khi nhả và chờ xử lý (16dp)
        final float LIFT_HIGH_PX = -res.getDisplayMetrics().density * 16;

        // --- THÊM HIỆU ỨNG DI CHUYỂN LÊN KHI NHẤN (HOVER/PRESS) ---
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            // ĐỔI TÊN THAM SỐ motionEvent ĐỂ KHÔNG BỊ TRÙNG VỚI BIẾN event (Model)
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 1. Khi nhấn xuống (Hover): Di chuyển nhẹ lên (8dp)
                        v.animate()
                                .translationY(LIFT_SLIGHTLY_PX)
                                .setDuration(100)
                                .start();
                        // Reset translationX về 0 (đề phòng)
                        v.setTranslationX(0f);
                        return true;

                    case MotionEvent.ACTION_UP:
                        // 2. Khi nhả ra (Chọn): Nâng hẳn lên (16dp) và kích hoạt logic click
                        v.animate()
                                .translationY(LIFT_HIGH_PX) // Nâng hẳn lên 16dp
                                .setDuration(150)
                                .start();

                        if (listener != null) {
                            // LẤY VỊ TRÍ MỚI NHẤT VÀ CHÍNH XÁC NHẤT TỪ HOLDER
                            int currentPosition = holder.getAdapterPosition();

                            if (currentPosition != RecyclerView.NO_POSITION) {
                                // GỌI listener.onCardClick VỚI ĐỐI TƯỢNG EVENT (Model) và vị trí mới nhất
                                listener.onCardClick(event, currentPosition);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        // Khi sự kiện bị hủy: Đưa về vị trí ban đầu
                        v.animate()
                                .translationY(0f)
                                .setDuration(150)
                                .start();
                        return true;
                }
                return false;
            }
        });
    }

    // --- HÀM MỚI: XỬ LÝ PHẢN HỒI ANIMATION TỪ ACTIVITY ---
    /**
     * Kích hoạt animation phản hồi (đúng/sai) cho lá bài.
     * @param view Lá bài (itemView) cần animate.
     * @param isCorrect True nếu chọn đúng, False nếu chọn sai.
     */
    public void animateFeedback(View view, boolean isCorrect) {
        if (isCorrect) {
            // Trường hợp chọn ĐÚNG: Hạ ngay lập tức về 0f (chuẩn bị xóa view)
            view.animate()
                    .translationY(0f)
                    .setDuration(150)
                    .start();
        } else {
            // Trường hợp chọn SAI: Lắc ngang và sau đó hạ xuống

            // Chuyển đổi dp sang px (Lắc 4dp)
            Resources res = view.getResources();
            final float SHAKE_DISTANCE_PX = res.getDisplayMetrics().density * 4;

            // Animation lắc ngang (tổng thời gian 500ms)
            // Lắc sang trái (-4), sang phải (+4), lắc nhẹ trái (-2), về giữa (0)
            view.animate()
                    .translationX(-SHAKE_DISTANCE_PX)
                    .setDuration(100) // Lắc trái
                    .withEndAction(() -> {
                        view.animate()
                                .translationX(SHAKE_DISTANCE_PX)
                                .setDuration(100) // Lắc phải
                                .withEndAction(() -> {
                                    view.animate()
                                            .translationX(-SHAKE_DISTANCE_PX / 2)
                                            .setDuration(100) // Lắc nhẹ trái
                                            .withEndAction(() -> {
                                                // Kết thúc lắc, đưa về vị trí giữa X=0 và hạ xuống Y=0
                                                view.animate()
                                                        .translationX(0f)
                                                        .translationY(0f) // Hạ về vị trí ban đầu
                                                        .setDuration(200)
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