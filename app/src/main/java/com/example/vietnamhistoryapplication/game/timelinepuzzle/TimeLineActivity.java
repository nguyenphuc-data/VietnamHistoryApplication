package com.example.vietnamhistoryapplication.game.timelinepuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.Era;
import com.example.vietnamhistoryapplication.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity implements CardAdapter.OnCardInteractionListener {

    private static final String TAG = "TimeLineActivity";

    // Danh sách cho khu vực chọn (đã xáo trộn)
    private RecyclerView rvCards;
    private CardAdapter cardAdapter;
    private List<Event> shuffledEvents = new ArrayList<>();

    // Danh sách các sự kiện đã sắp xếp theo THỨ TỰ (KEY đáp án)
    private List<Event> sortedEvents;

    // Danh sách cho khu vực đáp án (Ban đầu là null)
    private RecyclerView rvTimelineSlots;
    private TimelineSlotAdapter slotAdapter;
    private List<Event> placedEvents = new ArrayList<>();

    // Vị trí sự kiện tiếp theo cần điền (tương ứng với Index trong placedEvents và sortedEvents)
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        rvCards = findViewById(R.id.rvCards);
        rvTimelineSlots = findViewById(R.id.rvTimelineSlots);

        Intent intent = getIntent();
        Era eraObject = (Era) intent.getSerializableExtra("era");

        if (eraObject != null) {
            List<Event> rawEvents = eraObject.getEvents();

            if (rawEvents != null && !rawEvents.isEmpty()) {

                // 1. CHUẨN BỊ DỮ LIỆU

                // TẠO KEY (Đã sắp xếp theo trường 'order')
                sortedEvents = new ArrayList<>(rawEvents);
                // Sắp xếp theo trường 'order' (từ 1, 2, 3...)
                Collections.sort(sortedEvents, Comparator.comparingInt(Event::getOrder));

                // Tạo danh sách ô đáp án trống (số lượng ô bằng số lượng sự kiện)
                for (int i = 0; i < sortedEvents.size(); i++) {
                    placedEvents.add(null);
                }

                // Tạo danh sách lá bài để chọn (đã xáo trộn)
                shuffledEvents.addAll(rawEvents);
                Collections.shuffle(shuffledEvents);

                // 2. THIẾT LẬP RECYCLERVIEWS
                setupRecyclerViews();
            } else {
                Toast.makeText(this, "Thời đại này không có sự kiện nào.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Events list is empty or null.");
            }
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu thời đại (era).", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Era object is null.");
        }
    }

    private void setupRecyclerViews() {
        // Lá bài để chọn
        cardAdapter = new CardAdapter(shuffledEvents, this);
        rvCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCards.setAdapter(cardAdapter);

        // Setup RecyclerView cho các ô dòng thời gian (đáp án) - 4 cột
        slotAdapter = new TimelineSlotAdapter(placedEvents);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        rvTimelineSlots.setLayoutManager(layoutManager);
        rvTimelineSlots.setNestedScrollingEnabled(false);
        rvTimelineSlots.setAdapter(slotAdapter);
    }

    // --- LOGIC CHƠI GAME VÀ PHẢN HỒI ANIMATION ---

    @Override
    public void onCardClick(Event selectedEvent, int position) {

        if (currentStep >= sortedEvents.size()) {
            Toast.makeText(this, "Trò chơi đã hoàn thành!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy View của lá bài được chọn để thực hiện animation
        RecyclerView.ViewHolder viewHolder = rvCards.findViewHolderForAdapterPosition(position);
        View cardView = viewHolder != null ? viewHolder.itemView : null;

        // Giá trị order đúng = currentStep + 1
        int requiredOrder = currentStep + 1;

        // SO SÁNH ĐÁP ÁN
        if (selectedEvent.getOrder() == requiredOrder) {

            // ĐÚNG ĐÁP ÁN
            if (cardView != null) {
                // 1. Kích hoạt animation hạ xuống 0f (chuẩn bị xóa)
                cardAdapter.animateFeedback(cardView, true);
            }

            // DI CHUYỂN LÁ BÀI XUỐNG BẢNG DÒNG THỜI GIAN
            placedEvents.set(currentStep, selectedEvent);
            slotAdapter.notifyItemChanged(currentStep);

            // LOẠI BỎ LÁ BÀI ĐÃ CHỌN KHỎI KHU VỰC CHỌN
            shuffledEvents.remove(position);
            cardAdapter.notifyItemRemoved(position);
            cardAdapter.notifyItemRangeChanged(position, shuffledEvents.size());

            // CHUYỂN SANG BƯỚC TIẾP THEO
            currentStep++;

            if (currentStep >= sortedEvents.size()) {
                Toast.makeText(this, "XUẤT SẮC! Hoàn thành dòng thời gian!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Đúng! Tiếp tục với sự kiện thứ " + (currentStep + 1) + ".", Toast.LENGTH_SHORT).show();
            }

        } else {
            // SAI ĐÁP ÁN (Sai thứ tự)
            if (cardView != null) {
                // Kích hoạt animation lắc ngang và hạ xuống
                cardAdapter.animateFeedback(cardView, false);
            }
            Toast.makeText(this, "Sai rồi. Hãy chọn sự kiện có thứ tự tiếp theo.", Toast.LENGTH_SHORT).show();
        }
    }
}