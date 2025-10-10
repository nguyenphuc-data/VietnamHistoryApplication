package com.example.vietnamhistoryapplication.person.PersonDetail;

import com.google.firebase.firestore.DocumentReference;

public class PersonEventDetailItem {
    private String slug;
    private String title;
    private String overview;
    private String description;
    private String coverMediaRef;
    private DocumentReference eventRef;
    private String role;

    public PersonEventDetailItem() {}

    public PersonEventDetailItem(String slug, String title, String overview,
                                 String description, String coverMediaRef,
                                 DocumentReference eventRef, String role) {
        this.slug = slug;
        this.title = title;
        this.overview = overview;
        this.description = description;
        this.coverMediaRef = coverMediaRef;
        this.eventRef = eventRef;
        this.role = role;
    }

    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getDescription() { return description; }
    public String getCoverMediaRef() { return coverMediaRef; }
    public DocumentReference getEventRef() { return eventRef; }
    public String getRole() { return role; }

    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setDescription(String description) { this.description = description; }
    public void setCoverMediaRef(String coverMediaRef) { this.coverMediaRef = coverMediaRef; }
    public void setEventRef(DocumentReference eventRef) { this.eventRef = eventRef; }
    public void setRole(String role) { this.role = role; }
}