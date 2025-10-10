package com.example.vietnamhistoryapplication.event.eventDetail.ui;

public class HeaderItem extends EventDetailItem {
    private String coverUrl;
    private String title;
    private String subtitle;
//    private String period;

    // Constructor
    public HeaderItem(String title, String subtitle, String coverUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.coverUrl = coverUrl;
//        this.period = period;
    }

    // Getter Methods
    public String getCoverUrl() { return coverUrl; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
//    public String getPeriod() { return period; }

    // Method to get Item Type for Adapter
    @Override public int getType() {
        return EventDetailItem.TYPE_HEADER;
    }
}
