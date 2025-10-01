package com.example.vietnamhistoryapplication.common;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.R;

public class ImageLoader {

    public static void loadImage(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            String imageUrl = url;

            if (imageUrl.startsWith("https://drive.google.com")) {
                // Tách id từ link Drive
                String fileId = null;
                if (imageUrl.contains("/d/")) {
                    int start = imageUrl.indexOf("/d/") + 3;
                    int end = imageUrl.indexOf("/", start);
                    if (end == -1) {
                        end = imageUrl.length();
                    }
                    fileId = imageUrl.substring(start, end);
                }

                if (fileId != null) {
                    imageUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
                }
            }

            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(imageView);

        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }
    }
}
