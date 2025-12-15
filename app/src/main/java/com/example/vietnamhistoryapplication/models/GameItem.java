package com.example.vietnamhistoryapplication.models;

import java.util.List;

public class GameItem {
    private String name;
    private String type;
    private String description;
    private String thumbnailUrl;
    private List<QuizzItem> quizzes;

    public GameItem() {}

    public GameItem(String name, String type, String description, String thumbnailUrl) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public List<QuizzItem> getQuizzes() { return quizzes; }
    public void setQuizzes(List<QuizzItem> quizzes) { this.quizzes = quizzes; }
}
