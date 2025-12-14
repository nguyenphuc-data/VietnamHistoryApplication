package com.example.vietnamhistoryapplication.explore.articleDetail;



import java.util.ArrayList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Article {
    private String id;
    private String title;
    private String authors;
    private String coverMediaRef;
    private List<Event> events = new ArrayList<>();
    public Article(String id, String title, String authors, String coverMediaRef, List<Event> events) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.coverMediaRef = coverMediaRef;
        this.events = events != null ? events : new ArrayList<>();
    }
}
