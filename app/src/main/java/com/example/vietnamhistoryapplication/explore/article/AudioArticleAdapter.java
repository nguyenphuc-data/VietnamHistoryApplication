package com.example.vietnamhistoryapplication.explore.article;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.explore.articleDetail.ArticleFragment;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;

public class AudioArticleAdapter extends RecyclerView.Adapter<AudioArticleAdapter.ViewHolder> {

    private final List<AudioArticle> items;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public AudioArticleAdapter(List<AudioArticle> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioArticle item = items.get(position);
        holder.bind(item, position, holder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ==================== VIEW HOLDER ====================
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        android.widget.TextView tvTitle, tvExcerpt;
        MaterialButton btnPlay, btnRead;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvExcerpt = itemView.findViewById(R.id.tvExcerpt);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnRead = itemView.findViewById(R.id.btnRead);
            progressBar = itemView.findViewById(R.id.progress);
        }

        void bind(AudioArticle item, int position, Context context) {
            tvTitle.setText(item.getTitle());
            tvExcerpt.setText(item.getExcerpt());
            ImageLoader.loadImage(imgThumb, item.getThumbMediaRef());

            // Xác định trạng thái
            boolean isThisItem = AudioArticleAdapter.this.currentPlayingPosition == position;
            boolean isPreparing = isThisItem && AudioArticleAdapter.this.mediaPlayer != null
                    && !AudioArticleAdapter.this.mediaPlayer.isPlaying();
            boolean isPlaying = isThisItem && AudioArticleAdapter.this.mediaPlayer != null
                    && AudioArticleAdapter.this.mediaPlayer.isPlaying();

            // Cập nhật icon
            if (isPreparing) {
                btnPlay.setIconResource(R.drawable.ic_play_white);
            } else if (isPlaying) {
                btnPlay.setIconResource(R.drawable.ic_pause_white);
            } else {
                btnPlay.setIconResource(R.drawable.ic_play_white);
            }

            // Cập nhật progress
            if (isPlaying && AudioArticleAdapter.this.mediaPlayer != null) {
                int current = AudioArticleAdapter.this.mediaPlayer.getCurrentPosition();
                int total = AudioArticleAdapter.this.mediaPlayer.getDuration();
                if (total > 0) {
                    int progress = (int) (1000L * current / total);
                    progressBar.setProgress(progress);
                }
            }

            // Xử lý click
            btnPlay.setOnClickListener(v -> {
                if (AudioArticleAdapter.this.currentPlayingPosition == position) {
                    // Đang phát hoặc đang chuẩn bị
                    if (AudioArticleAdapter.this.mediaPlayer != null) {
                        if (AudioArticleAdapter.this.mediaPlayer.isPlaying()) {
                            AudioArticleAdapter.this.mediaPlayer.pause();
                        } else {
                            AudioArticleAdapter.this.mediaPlayer.start();
                            AudioArticleAdapter.this.startProgressUpdate(position);
                        }
                        notifyItemChanged(position);
                    }
                } else {
                    // Phát bài mới
                    AudioArticleAdapter.this.stopCurrentPlayback();
                    AudioArticleAdapter.this.currentPlayingPosition = position;
                    AudioArticleAdapter.this.startPlayback(item.getAudioUrl(), position, context);
                    // UI sẽ cập nhật trong onPrepared
                }
            });
            // Xử lý nút Read
            btnRead.setOnClickListener(v -> {
                ArticleFragment fragment = new ArticleFragment();
                Bundle bundle = new Bundle();
                bundle.putString("documentId", item.getId());
                fragment.setArguments(bundle);

                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    // ==================== MEDIA PLAYER LOGIC ====================
    private void startPlayback(String url, int position, Context context) {
        stopCurrentPlayback();

        Log.d("AudioPlayer", "Playing: " + url);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                currentPlayingPosition = position; // Cập nhật vị trí
                notifyItemChanged(position);
                startProgressUpdate(position);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error: " + what + ", " + extra);
                Toast.makeText(context, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
                stopCurrentPlayback();
                notifyItemChanged(position);
                return true;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                stopCurrentPlayback();
                notifyItemChanged(position);
            });

        } catch (IOException e) {
            Log.e("MediaPlayer", "setDataSource failed", e);
            Toast.makeText(context, "URL không hợp lệ", Toast.LENGTH_SHORT).show();
            stopCurrentPlayback();
        }
    }

    private void stopCurrentPlayback() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);

        int oldPosition = currentPlayingPosition;
        currentPlayingPosition = -1;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
    }

    private void startProgressUpdate(int position) {
        handler.removeCallbacksAndMessages(null);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying() && currentPlayingPosition == position) {
                    int current = mediaPlayer.getCurrentPosition();
                    int total = mediaPlayer.getDuration();
                    if (total > 0) {
                        notifyItemChanged(position); // Cập nhật progress + icon
                    }
                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        stopCurrentPlayback();
        super.onDetachedFromRecyclerView(recyclerView);
    }

}