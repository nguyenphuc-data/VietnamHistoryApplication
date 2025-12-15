package com.example.vietnamhistoryapplication.models;

import java.io.Serializable;
import java.util.List;

public class Era implements Serializable {
    private String eraId = "";
    private String name = "";
    private String shortDesc = "";
    private String thumbnailUrl;
    private List<Event> events;

    public Era() {}

    public Era(String eraId, String name, String shortDesc, String thumbnailUrl, List<Event> events) {
        this.eraId = eraId;
        this.name = name;
        this.shortDesc = shortDesc;
        this.thumbnailUrl = thumbnailUrl;
        this.events = events;
    }

    public String getEraId() { return eraId; }
    public String getName() { return name; }
    public String getShortDesc() { return shortDesc; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public List<Event> getEvents() { return events; }

    public void setEraId(String eraId) { this.eraId = eraId; }
}