package com.example.vietnamhistoryapplication.forum;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ForumPost {
    public String postId;
    public String title;
    public String content;
    public String authorName;
    public String authorPhoto;
    public String authorId;
    public long replyCount = 0;
    public long likeCount = 0;
    public Timestamp createdAt;
    public List<String> likes = new ArrayList<>();

    public ForumPost() {}

    public ForumPost(String postId, String title, String content, String authorName,
                     String authorPhoto, String authorId, long replyCount,
                     long likeCount, Timestamp createdAt, List<String> likes) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorPhoto = authorPhoto;
        this.authorId = authorId;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.likes = likes != null ? likes : new ArrayList<>();
    }
}