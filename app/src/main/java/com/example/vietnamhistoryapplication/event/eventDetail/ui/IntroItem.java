package com.example.vietnamhistoryapplication.event.eventDetail.ui;

public class IntroItem extends EventDetailItem {
    private String content;

    private String imgUrl;

    // Constructor
    public IntroItem(String content,String imgUrl ) {
        this.content = content;

        this.imgUrl = imgUrl;
    }

    // Getter methods
    public String getContent() {
        return content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public int getType() {
        return TYPE_INTRO;
    }
}
