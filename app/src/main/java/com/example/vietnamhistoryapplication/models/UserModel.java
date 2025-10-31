package com.example.vietnamhistoryapplication.models;

public class UserModel {
    private String uid;
    private String name;
    private String username;
    private String email;
    private String photo;
    private String bio;
    private long createdAt;

    public UserModel() {}

    public UserModel(String uid, String name, String username, String email, String photo, String bio, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.photo = photo;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    // 🟢 Getter & Setter
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
