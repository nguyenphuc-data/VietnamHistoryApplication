package com.example.vietnamhistoryapplication.game.timelinepuzzle;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.Era;
import com.example.vietnamhistoryapplication.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity implements CardAdapter.OnCardInteractionListener {

    private RecyclerView rvCards, rvTimelineSlots;
    private CardAdapter cardAdapter;
    private TimelineSlotAdapter slotAdapter;
    private List<Event> shuffledEvents = new ArrayList<>();
    private List<Event> sortedEvents;
    private List<Event> placedEvents = new ArrayList<>();
    private int currentStep = 0;

    private ImageView ivWarrior, ivTurret;
    private ImageView hp1, hp2, hp3;
    private AnimationDrawable warriorIdleAnimation;

    private View rootLayout;
    private boolean isProcessing = false;
    private Toast mToast;

    private int wrongCount = 0;
    private int attackCount = 0;
    private static final int MAX_WRONG = 5;
    private static final int MAX_ATTACK_STEPS = 3;
    private static final float STEP_DISTANCE = 0.126f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        rvCards = findViewById(R.id.rvCards);
        rvTimelineSlots = findViewById(R.id.rvTimelineSlots);
        ivWarrior = findViewById(R.id.ivWarrior);
        ivTurret = findViewById(R.id.ivTurret);
        hp1 = findViewById(R.id.hp1);
        hp2 = findViewById(R.id.hp2);
        hp3 = findViewById(R.id.hp3);
        rootLayout = findViewById(R.id.main);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupGameAnimations();
        loadEraData();
    }

    private void loadEraData() {
        Era era = (Era) getIntent().getSerializableExtra("era");
        if (era == null || era.getEvents() == null || era.getEvents().isEmpty()) {
            showToast("Không có dữ liệu!");
            finish();
            return;
        }

        List<Event> rawEvents = era.getEvents();
        sortedEvents = new ArrayList<>(rawEvents);
        Collections.sort(sortedEvents, Comparator.comparingInt(Event::getOrder));

        for (int i = 0; i < sortedEvents.size(); i++) placedEvents.add(null);

        shuffledEvents.addAll(rawEvents);
        Collections.shuffle(shuffledEvents);

        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        cardAdapter = new CardAdapter(shuffledEvents, this);
        rvCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCards.setAdapter(cardAdapter);

        slotAdapter = new TimelineSlotAdapter(placedEvents);
        rvTimelineSlots.setLayoutManager(new GridLayoutManager(this, 4));
        rvTimelineSlots.setNestedScrollingEnabled(false);
        rvTimelineSlots.setAdapter(slotAdapter);
    }

    private void setupGameAnimations() {
        ivWarrior.setBackgroundResource(R.drawable.warrior2_idle);
        warriorIdleAnimation = (AnimationDrawable) ivWarrior.getBackground();

        ivTurret.setBackgroundResource(R.drawable.turret_idle);
        AnimationDrawable turretAnim = (AnimationDrawable) ivTurret.getBackground();

        ivWarrior.post(() -> warriorIdleAnimation.start());
        ivTurret.post(() -> turretAnim.start());
    }

    private void lockScreen() {
        isProcessing = true;
        rootLayout.setClickable(false);
    }

    private void unlockScreen() {
        rootLayout.postDelayed(() -> {
            isProcessing = false;
            rootLayout.setClickable(true);
        }, 1200);
    }

    private void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (mToast != null) mToast.cancel();
        }, 1000);
    }

    private void playWarriorWalkAndMove() {
        wrongCount++;
        if (wrongCount > MAX_WRONG) {
            playWarriorAttack();
        } else {
            ivWarrior.setBackgroundResource(R.drawable.warrior2_walk);
            AnimationDrawable walkAnim = (AnimationDrawable) ivWarrior.getBackground();
            walkAnim.start();

            float currentBias = ((ConstraintLayout.LayoutParams) ivWarrior.getLayoutParams()).horizontalBias;
            float newBias = Math.min(currentBias + STEP_DISTANCE, 0.79f);

            ValueAnimator animator = ValueAnimator.ofFloat(currentBias, newBias);
            animator.setDuration(800);
            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ivWarrior.getLayoutParams();
                params.horizontalBias = value;
                ivWarrior.setLayoutParams(params);
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    backToIdle();
                }
            });
            animator.start();
        }
    }

    private void playWarriorAttack() {
        attackCount++;

        ivWarrior.setBackgroundResource(R.drawable.warior2_atack);
        AnimationDrawable attackAnim = (AnimationDrawable) ivWarrior.getBackground();
        attackAnim.start();

        ivTurret.animate().scaleX(0.88f).scaleY(0.88f).setDuration(120)
                .withEndAction(() -> ivTurret.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                .start();

        switch (attackCount) {
            case 1: hp1.setBackgroundResource(R.drawable.hp_left_empty); break;
            case 2: hp2.setBackgroundResource(R.drawable.hp_middle_empty); break;
            case 3: hp3.setBackgroundResource(R.drawable.hp_right_empty); break;
        }

        ivWarrior.postDelayed(this::backToIdle, 800);

        if (attackCount >= MAX_ATTACK_STEPS) {
            new Handler(Looper.getMainLooper()).postDelayed(this::showGameOverDialog, 1000);
        }
    }

    private void backToIdle() {
        ivWarrior.setBackgroundResource(R.drawable.warrior2_idle);
        warriorIdleAnimation = (AnimationDrawable) ivWarrior.getBackground();
        ivWarrior.post(() -> warriorIdleAnimation.start());
    }

    // =================== DIALOG THẮNG / THUA KIỂU CLASH OF CLANS ===================
    private String getCorrectTimelineText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Đáp án đúng theo thứ tự:\n\n");

        for (Event event : sortedEvents) {
            sb.append(event.getYear())
                    .append(" - ")
                    .append(event.getName())
                    .append("\n");
        }

        return sb.toString().trim();
    }
    private void showVictoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_victory, null);

        TextView tvCorrectAnswers = view.findViewById(R.id.tv_correct_answers);
        tvCorrectAnswers.setText(getCorrectTimelineText()); // HIỂN THỊ ĐÁP ÁN

        Button btnReplay = view.findViewById(R.id.btnReplay);
        Button btnMenu = view.findViewById(R.id.btnMenu);

        AlertDialog dialog = builder.setView(view).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        btnReplay.setOnClickListener(v -> {
            dialog.dismiss();
            restartGame();
        });

        btnMenu.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x80000000));
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_game_over, null);

        // Dòng mới: hiển thị đáp án ngay cả khi thua
        TextView tvCorrectAnswers = view.findViewById(R.id.tv_correct_answers);
        tvCorrectAnswers.setText(getCorrectTimelineText());

        Button btnReplay = view.findViewById(R.id.btnReplay);
        Button btnMenu = view.findViewById(R.id.btnMenu);

        AlertDialog dialog = builder.setView(view).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        btnReplay.setOnClickListener(v -> { dialog.dismiss(); restartGame(); });
        btnMenu.setOnClickListener(v -> { dialog.dismiss(); finish(); });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x80000000));
    }

    private void restartGame() {
        finish();
        startActivity(getIntent());
    }

    // =================== KHI NGƯỜI CHƠI CHỌN THẺ ===================
    @Override
    public void onCardClick(Event selectedEvent, int position) {
        if (isProcessing || currentStep >= sortedEvents.size()) return;

        lockScreen();

        RecyclerView.ViewHolder viewHolder = rvCards.findViewHolderForAdapterPosition(position);
        View cardView = viewHolder != null ? viewHolder.itemView : null;

        if (selectedEvent.getOrder() == currentStep + 1) {
            // ĐÚNG
            if (cardView != null) cardAdapter.animateFeedback(cardView, true);

            placedEvents.set(currentStep, selectedEvent);
            slotAdapter.notifyItemChanged(currentStep);

            shuffledEvents.remove(position);
            cardAdapter.notifyItemRemoved(position);
            cardAdapter.notifyItemRangeChanged(position, shuffledEvents.size());

            currentStep++;

            if (currentStep >= sortedEvents.size()) {
                new Handler(Looper.getMainLooper()).postDelayed(this::showVictoryDialog, 800);
            } else {
                showToast("Đúng rồi!");
            }

        } else {
            // SAI
            if (cardView != null) cardAdapter.animateFeedback(cardView, false);
            playWarriorWalkAndMove();
            showToast("SAI RỒI!");
        }

        unlockScreen();
    }
}