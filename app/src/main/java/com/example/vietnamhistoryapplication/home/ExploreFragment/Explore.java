package com.example.vietnamhistoryapplication.home.ExploreFragment;
public class Explore {
    private String slug;
    private String title;
    private String description;
    private String coverMediaRef;
    private int sortOrder;

    public Explore() {}

    public Explore(String slug, String title, String description, String coverMediaRef, int sortOrder) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.coverMediaRef = coverMediaRef;
        this.sortOrder = sortOrder;
    }

    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCoverMediaRef() { return coverMediaRef; }
    public int getSortOrder() { return sortOrder; }
}