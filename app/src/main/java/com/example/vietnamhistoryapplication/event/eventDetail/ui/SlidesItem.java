package com.example.vietnamhistoryapplication.event.eventDetail.ui;
import java.util.List;
import java.util.Map;

public class SlidesItem extends EventDetailItem {
    private List<Map<String, Object>> slides;  // List of slides
    private String title;  // Title cho slides (nếu cần)

    // Constructor nhận slides và title
    public SlidesItem(List<Map<String, Object>> slides, String title) {
        this.slides = slides;
        this.title = title;
    }

    // Getter for slides
    public List<Map<String, Object>> getSlides() {
        return slides;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    @Override
    public int getType() {
        return TYPE_SLIDES;
    }
}
