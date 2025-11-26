package com.example.vietnamhistoryapplication.person.PersonDetail;

public class PersonEventDetailItem {
    private String slug;
    private String title;
    private String overview;
    private String description;
    private String coverMediaRef;
    private String eventRef;
    private String role;

    public PersonEventDetailItem() {}

    public PersonEventDetailItem(String slug, String title, String overview,
                                 String description, String coverMediaRef,
                                 String eventRef, String role) {
        this.slug = slug;
        this.title = title;
        this.overview = overview;
        this.description = description;
        this.coverMediaRef = coverMediaRef;
        this.eventRef = eventRef; // Cập nhật constructor
        this.role = role;
    }

    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getDescription() { return description; }
    public String getCoverMediaRef() { return coverMediaRef; }
    public String getEventRef() { return eventRef; } // Cập nhật getter
    public String getRole() { return role; }

    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setDescription(String description) { this.description = description; }
    public void setCoverMediaRef(String coverMediaRef) { this.coverMediaRef = coverMediaRef; }
    public void setEventRef(String eventRef) { this.eventRef = eventRef; } // Cập nhật setter
    public void setRole(String role) { this.role = role; }
}