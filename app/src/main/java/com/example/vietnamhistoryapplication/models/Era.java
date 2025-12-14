package com.example.vietnamhistoryapplication.models;

import java.io.Serializable;
import java.util.List;

public class Era implements Serializable {
    private String eraId = "";
    private String name = "";
    private String shortDesc = "";
    private String thumbnailUrl;
    private List<Event> events;

    // Constructor rỗng cho Firestore
    public Era() {}

    public Era(String eraId, String name, String shortDesc, String thumbnailUrl, List<Event> events) {
        this.eraId = eraId;
        this.name = name;
        this.shortDesc = shortDesc;
        this.thumbnailUrl = thumbnailUrl;
        this.events = events;
    }

    // Getters
    public String getEraId() { return eraId; }
    public String getName() { return name; }
    public String getShortDesc() { return shortDesc; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public List<Event> getEvents() { return events; }

    // Setters (nếu cần)
    public void setEraId(String eraId) { this.eraId = eraId; }
}