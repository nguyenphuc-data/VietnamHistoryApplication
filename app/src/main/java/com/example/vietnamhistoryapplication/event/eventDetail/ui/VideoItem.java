package com.example.vietnamhistoryapplication.event.eventDetail.ui;

public class VideoItem extends EventDetailItem {
    public String videoUrl;  // YouTube link
    public String title;
    public VideoItem(String title, String videoUrl) {

        this.title = title;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }
    public String getVideoUrlOrId() {
        return videoUrl;
    }

    @Override
    public int getType() {
        return TYPE_VIDEO;
    }
}
