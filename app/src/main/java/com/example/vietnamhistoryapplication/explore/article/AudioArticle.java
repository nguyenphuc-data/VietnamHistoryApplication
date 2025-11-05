// com.example.vietnamhistoryapplication/explore/audio/AudioArticle.java
package com.example.vietnamhistoryapplication.explore.article;

public class AudioArticle {
    private String id;
    private String title;
    private String excerpt;
    private String thumbMediaRef;
    private String audioUrl;
    public AudioArticle() {}

    public AudioArticle(String id, String title, String excerpt, String thumbMediaRef, String audioUrl) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.thumbMediaRef = thumbMediaRef;
        this.audioUrl = audioUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getExcerpt() { return excerpt; }
    public String getThumbMediaRef() { return thumbMediaRef; }
    public String getAudioUrl() { return audioUrl; }
}