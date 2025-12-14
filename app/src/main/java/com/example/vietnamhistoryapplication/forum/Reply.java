package com.example.vietnamhistoryapplication.forum;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Reply {
    public String replyId;
    public String content;
    public String authorName;
    public String authorPhoto;
    public String authorId;
    public long likeCount = 0;
    public Timestamp createdAt;
    public List<String> likes = new ArrayList<>();

    public Reply() {}

    public Reply(String replyId, String content, String authorName, String authorPhoto,
                 String authorId, long likeCount, Timestamp createdAt, List<String> likes) {
        this.replyId = replyId;
        this.content = content;
        this.authorName = authorName;
        this.authorPhoto = authorPhoto;
        this.authorId = authorId;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.likes = likes != null ? likes : new ArrayList<>();
    }
}