package com.example.vietnamhistoryapplication.common;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YouTubeUtils {
    private static final Pattern[] PATS = new Pattern[] {
            Pattern.compile("v=([a-zA-Z0-9_-]{6,})"),
            Pattern.compile("youtu\\.be/([a-zA-Z0-9_-]{6,})"),
            Pattern.compile("embed/([a-zA-Z0-9_-]{6,})")
    };

    @Nullable public static String extractVideoId(@Nullable String url) {
        if (TextUtils.isEmpty(url)) return null;
        for (Pattern p : PATS) {
            Matcher m = p.matcher(url);
            if (m.find()) return m.group(1);
        }
        return null;
    }

    public static void loadVideo(YouTubePlayerView view, String idOrUrl, LifecycleOwner owner) {
        String id = extractVideoId(idOrUrl);
        if (TextUtils.isEmpty(id)) id = idOrUrl;
        owner.getLifecycle().addObserver(view);
        final String finalId = id;
        view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override public void onReady(YouTubePlayer player) {
                player.cueVideo(finalId, 0f);
            }
        });
    }

    private YouTubeUtils() {}
}
