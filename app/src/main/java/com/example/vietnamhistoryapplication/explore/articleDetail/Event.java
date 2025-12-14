package com.example.vietnamhistoryapplication.explore.articleDetail;


import java.io.Serializable;

public class Event implements Serializable {
    public String details;
    public ImageInfo image;

    public static class ImageInfo implements Serializable {
        public String content;
        public String link;

        public String getContent() { return content; }
        public String getLink() { return link; }
    }

    public String getDetails() { return details; }
    public ImageInfo getImage() { return image; }
}